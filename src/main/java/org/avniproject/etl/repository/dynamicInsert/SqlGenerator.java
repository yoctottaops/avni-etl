package org.avniproject.etl.repository.dynamicInsert;

import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.metadata.ColumnMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.avniproject.etl.domain.metadata.diff.Strings.COMMA;
import static org.avniproject.etl.repository.dynamicInsert.SqlFile.*;

public class SqlGenerator {

    private static String toString(Integer id) {
        return id == null ? "" : id.toString();
    }

    public String generateSql(TableMetadata tableMetadata, Date startTime, Date endTime) {
        switch (tableMetadata.getType()) {
            case Household:
            case Individual: {
                return getSql("/insertSql/individual.sql", tableMetadata, startTime, endTime);
            }
            case Person: {
                return getSql("/insertSql/person.sql", tableMetadata, startTime, endTime);
            }
            case Encounter: {
                return getSql("/insertSql/generalEncounter.sql", tableMetadata, startTime, endTime);
            }
            case ProgramEnrolment: {
                return getSql("/insertSql/programEnrolment.sql", tableMetadata, startTime, endTime);
            }
            case ProgramExit: {
                return getSql("/insertSql/programEnrolmentExit.sql", tableMetadata, startTime, endTime);
            }
            case ProgramEncounter: {
                return getSql("/insertSql/programEncounter.sql", tableMetadata, startTime, endTime);
            }
            case ProgramEncounterCancellation: {
                return getSql("/insertSql/programEncounterCancel.sql", tableMetadata, startTime, endTime);
            }
            case IndividualEncounterCancellation: {
                return getSql("/insertSql/generalEncounterCancel.sql", tableMetadata, startTime, endTime);
            }
            default:
                throw new RuntimeException("Could not generate sql for" + tableMetadata.getType().toString());
        }
    }

    private String getSql(String path, TableMetadata tableMetadata, Date startTime, Date endTime) {
        String template = readFile(path);
        return template.replace("${schema_name}", wrapInQuotes(ContextHolder.getDbSchema()))
                .replace("${table_name}", wrapInQuotes(tableMetadata.getName()))
                .replace("${observations_to_insert_list}", getListOfObservations(tableMetadata))
                .replace("${concept_maps}", getConceptMaps(tableMetadata))
                .replace("${cross_join_concept_maps}", "cross join " + getConceptMapName(tableMetadata))
                .replace("${subject_type_id}", toString(tableMetadata.getSubjectTypeId()))
                .replace("${selections}", buildObservationSelection(tableMetadata, "observations"))
                .replace("${exit_obs_selections}", buildObservationSelection(tableMetadata, "program_exit_observations"))
                .replace("${cancel_obs_selections}", buildObservationSelection(tableMetadata, "cancel_observations"))
                .replace("${encounter_type_id}", toString(tableMetadata.getEncounterTypeId()))
                .replace("${program_id}", toString(tableMetadata.getProgramId()))
                .replace("${start_time}", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(startTime))
                .replace("${end_time}", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(endTime));
    }

    private String getConceptMapName(TableMetadata tableMetadata) {
        return tableMetadata.getName() + "_" + "concept_maps";
    }

    private String getConceptMaps(TableMetadata tableMetadata) {
        String template = readFile("/insertSql/conceptMap.sql");
        List<String> names = new ArrayList<>();
        names.add("'dummy'");
        names.addAll(tableMetadata.getNonDefaultColumnMetadataList().stream().map(columnMetadata -> wrapInSingleQuotes(columnMetadata.getConceptUuid())).collect(Collectors.toList()));

        return template
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
        String obsColumn =  "entity." + obsColumnName;
        if (columns.isEmpty()) return "";

        String columnSelects = columns.parallelStream().map(column -> {
            String conceptUUID = column.getConceptUuid();
            String columnName = column.getName();
            switch (column.getConceptType()) {
                case SingleSelect:
                case MultiSelect: {
                    return String.format("public.get_coded_string_value(%s->'%s', %s.map)::TEXT as \"%s\"",
                            obsColumn, conceptUUID, getConceptMapName(tableMetadata), column.getName());
                }
                case Date: {
                    return String.format("(%s->>'%s')::DATE as \"%s\"", obsColumn, conceptUUID, columnName);
                }
                case DateTime: {
                    return String.format("(%s->>'%s')::TIMESTAMP as \"%s\"", obsColumn, conceptUUID, columnName);
                }
                case Time: {
                    return String.format("(%s->>'%s')::TIME as \"%s\"", obsColumn, conceptUUID, columnName);
                }
                case Numeric: {
                    return String.format("(%s->>'%s')::NUMERIC as \"%s\"", obsColumn, conceptUUID, columnName);
                }
                case Subject:
                case Location: {
                    return String.format("(%s->>'%s') as \"%s\"", obsColumn, conceptUUID, columnName);
                }
                default: {
                    return String.format("(%s->>'%s')::TEXT as \"%s\"", obsColumn, conceptUUID, columnName);
                }
            }
        }).collect(Collectors.joining(",\n"));
        return "," + columnSelects;
    }

}
