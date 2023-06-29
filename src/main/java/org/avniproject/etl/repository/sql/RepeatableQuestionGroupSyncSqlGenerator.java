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
    private final Map<String, String> parentTypeFileMap = new HashMap<>() {{
        this.put("IndividualProfile", "subjectRepeatableQGObservations.sql");
        this.put("Encounter", "encounterRepeatableQGObservations.sql");
        this.put("ProgramEnrolment", "programEnrolmentRepeatableQGObservations.sql");
        this.put("ProgramEncounter", "programEncounterRepeatableQGObservations.sql");
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
            return getSql(parentTypeFileMap.get(tableMetadata.getEncounterTypeUuid()), tableMetadata, startTime, endTime);
        }
        throw new RuntimeException("Could not generate sql for" + tableMetadata.getType().toString());
    }

    public String getSql(String fileName, TableMetadata tableMetadata, Date startTime, Date endTime) {
        String template = readSqlFile(fileName);
        String obsColumnName = tableMetadata.getType().equals(TableMetadata.Type.Address) ? "location_properties" : "observations";
        String text = template.replace("${schema_name}", wrapInQuotes(OrgIdentityContextHolder.getDbSchema()))
                .replace("${table_name}", wrapInQuotes(tableMetadata.getName()))
                .replace("${observations_to_insert_list}", getListOfObservations(tableMetadata))
                .replace("${concept_maps}", getConceptMaps(tableMetadata))
                .replace("${cross_join_concept_maps}", "cross join " + getConceptMapName(tableMetadata))
                .replace("${subject_type_uuid}", toString(tableMetadata.getSubjectTypeUuid()))
                .replace("${selections}", buildObservationSelection(tableMetadata, obsColumnName))
                .replace("${exit_obs_selections}", buildObservationSelection(tableMetadata, "program_exit_observations"))
                .replace("${cancel_obs_selections}", buildObservationSelection(tableMetadata, "cancel_observations"))
                .replace("${encounter_type_uuid}", toString(tableMetadata.getEncounterTypeUuid()))
                .replace("${program_uuid}", toString(tableMetadata.getProgramUuid()))
                .replace("${start_time}", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(startTime))
                .replace("${end_time}", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(endTime));
        if (tableMetadata.getType().equals(TableMetadata.Type.Person) && tableMetadata.hasColumn("middle_name")) {
            text = text.replace("${middle_name}", ",middle_name").replace("${middle_name_select}", ", entity.middle_name                                                               as \"middle_name\"");
        } else {
            text = text.replace("${middle_name}", "").replace("${middle_name_select}", "");
        }
        return text;
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
