package org.avniproject.etl.repository.sql;

import org.avniproject.etl.domain.OrgIdentityContextHolder;
import org.avniproject.etl.domain.metadata.ColumnMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.avniproject.etl.domain.metadata.diff.Strings.COMMA;
import static org.avniproject.etl.repository.sql.SqlFile.readSqlFile;

public class RepeatableQuestionGroupSyncSqlGenerator {
    private final Map<TableMetadata.TableType, String> parentTypeFileMap = new HashMap<>() {{
        this.put(TableMetadata.TableType.IndividualProfile, "subjectRepeatableQGObservations.sql");
        this.put(TableMetadata.TableType.Encounter, "generalEncounterRepeatableQGObservations.sql");
        this.put(TableMetadata.TableType.ProgramEnrolment, "programEnrolmentRepeatableQGObservations.sql");
        this.put(TableMetadata.TableType.ProgramEncounter, "programEncounterRepeatableQGObservations.sql");
    }};
    private static final String obsSqlTemplate = readSqlFile("conceptMap.sql");

    private static String toString(String uuid) {
        return uuid == null ? "" : uuid;
    }

    public boolean supports(TableMetadata tableMetadata) {
        return tableMetadata.getType().equals(TableMetadata.Type.RepeatableQuestionGroup);
    }

    public String generateSql(TableMetadata tableMetadata, Date startTime, Date endTime) {
        if (supports(tableMetadata)) {
            String fileName = "repeatableQG/" + parentTypeFileMap.get(tableMetadata.getParentTableType());
            return getSql(fileName, tableMetadata, startTime, endTime);
        }
        throw new RuntimeException("Could not generate sql for" + tableMetadata.getType().toString());
    }

    public String getSql(String fileName, TableMetadata tableMetadata, Date startTime, Date endTime) {
        String template = readSqlFile(fileName);
        return template.replace("${schema_name}", wrapInQuotes(OrgIdentityContextHolder.getDbSchema()))
                .replace("${table_name}", wrapInQuotes(tableMetadata.getName()))
                .replace("${observations_to_insert_list}", getListOfObservations(tableMetadata))
                .replace("${concept_maps}", getConceptMaps(tableMetadata))
                .replace("${cross_join_concept_maps}", "cross join " + getConceptMapName(tableMetadata))
                .replace("${subject_type_uuid}", toString(tableMetadata.getSubjectTypeUuid()))
                .replace("${selections}", buildObservationSelection(tableMetadata, "observations"))
                .replace("${encounter_type_uuid}", toString(tableMetadata.getEncounterTypeUuid()))
                .replace("${program_uuid}", toString(tableMetadata.getProgramUuid()))
                .replace("${repeatable_question_group_concept_uuid}", toString(tableMetadata.getRepeatableQuestionGroupConceptUuid()))
                .replace("${start_time}", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(startTime))
                .replace("${end_time}", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(endTime));
    }

    private String getConceptMapName(TableMetadata tableMetadata) {
        return tableMetadata.getName() + "_" + "concept_maps";
    }

    private String getConceptMaps(TableMetadata tableMetadata) {
        List<String> names = new ArrayList<>();
        names.add("'dummy'");
        names.addAll(tableMetadata.getNonDefaultColumnMetadataList().stream().map(columnMetadata -> wrapInSingleQuotes(columnMetadata.getConceptUuid())).collect(Collectors.toList()));

        return obsSqlTemplate
                .replace("${mapName}", getConceptMapName(tableMetadata))
                .replace("${conceptUuids}", String.join(", ", names));
    }

    private StringBuffer getListOfObservations(TableMetadata tableMetadata) {
        StringBuffer list = new StringBuffer();
        if (tableMetadata.hasNonDefaultColumns()) {
            list.append(COMMA);
        }
        List<ColumnMetadata> columns = tableMetadata.getNonDefaultColumnMetadataList();
        if (columns.isEmpty()) return list;

        List<String> names = columns.stream().map(columnMetadata -> wrapInQuotes(columnMetadata.getName())).collect(Collectors.toList());
        String columnNames = String.join(", ", names);
        return list.append(columnNames);
    }

    private String wrapInQuotes(String name) {
        return "\"" + name + "\"";
    }

    private String wrapInSingleQuotes(String name) {
        return "'" + name + "'";
    }

    private String buildObservationSelection(TableMetadata tableMetadata, String obsColumnName) {
        List<ColumnMetadata> columns = tableMetadata.getNonDefaultColumnMetadataList();
        if (columns.isEmpty()) return "";

        String columnSelects = columns.parallelStream().map(column -> {
            String obsColumn = column.getColumn().isSyncAttributeColumn() ?
                    "ind.observations"
                    : String.format("entity.%s", obsColumnName);
            String columnName = column.getName();
            switch (column.getConceptType()) {
                case SingleSelect:
                case MultiSelect: {
                    return String.format("public.get_coded_string_value(%s%s, %s.map)::TEXT as \"%s\"",
                            obsColumn, column.getJsonbExtractor(), getConceptMapName(tableMetadata), column.getName());
                }
                case Date: {
                    return String.format("((%s%s)::timestamptz AT time zone 'asia/kolkata')::date as \"%s\"", obsColumn, column.getTextExtractor(), columnName);
                }
                case DateTime: {
                    return String.format("(%s%s)::timestamptz AT time zone 'asia/kolkata' as \"%s\"", obsColumn, column.getTextExtractor(), columnName);
                }
                case Time: {
                    return String.format("(%s%s)::TIME as \"%s\"", obsColumn, column.getTextExtractor(), columnName);
                }
                case Numeric: {
                    return String.format("(%s%s)::NUMERIC as \"%s\"", obsColumn, column.getTextExtractor(), columnName);
                }
                case Subject:
                case Encounter:
                case Location: {
                    return String.format("(%s%s) as \"%s\"", obsColumn, column.getTextExtractor(), columnName);
                }
                default: {
                    return String.format("(%s%s)::TEXT as \"%s\"", obsColumn, column.getTextExtractor(), columnName);
                }
            }
        }).collect(Collectors.joining(",\n"));
        return "," + columnSelects;
    }

}
