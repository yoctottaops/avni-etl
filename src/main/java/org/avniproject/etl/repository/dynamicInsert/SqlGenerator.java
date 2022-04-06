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

    public String generateSql(TableMetadata tableMetadata, Date startTime, Date endTime) {
        switch (tableMetadata.getType()) {
            case Individual: {
                return getSql("/insertSql/individual.sql", tableMetadata, startTime, endTime);
            }
            case Person: {
                return getSql("/insertSql/person.sql", tableMetadata, startTime, endTime);
            }
            default:
        }
        return null;
    }

    private String getSql(String path, TableMetadata tableMetadata, Date startTime, Date endTime) {
        String template = readFile(path);
        return template.replace("${schema_name}", wrapInQuotes(ContextHolder.getDbSchema()))
                .replace("${table_name}", wrapInQuotes(tableMetadata.getName()))
                .replace("${observations_to_insert_list}", getListOfObservations(tableMetadata))
                .replace("${concept_maps}", getConceptMaps(tableMetadata))
                .replace("${cross_join_concept_maps}", "cross join " + getConceptMapName(tableMetadata))
                .replace("${subject_type_id}", tableMetadata.getSubjectTypeId().toString())
                .replace("${selections}", buildObservationSelection("individual", tableMetadata))
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
        names.addAll(tableMetadata.getNonDefaultColumnMetadataList().stream().map(columnMetadata -> wrapInQuotes(columnMetadata.getConceptUuid())).collect(Collectors.toList()));

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

    private String buildObservationSelection(String entity, TableMetadata tableMetadata) {
        return buildObservationSelection(entity, tableMetadata.getNonDefaultColumnMetadataList(), "observations", getConceptMapName(tableMetadata));
    }

    private String buildCancelObservationSelection(String entity, TableMetadata tableMetadata) {
        return buildObservationSelection(entity, tableMetadata.getNonDefaultColumnMetadataList(), "cancel_observations", getConceptMapName(tableMetadata));
    }

    private String buildExitObservationSelection(TableMetadata tableMetadata) {
        return buildObservationSelection("programEnrolment", tableMetadata.getNonDefaultColumnMetadataList(), "program_exit_observations", getConceptMapName(tableMetadata));
    }

    private String buildObservationSelection(String entity, List<ColumnMetadata> columns, String obsColumnName, String conceptMapName) {
        String obsColumn = entity + "." + obsColumnName;
        if (columns.isEmpty()) return "";

        String columnSelects = columns.parallelStream().map(column -> {
            String conceptUUID = column.getConceptUuid();
            String columnName = column.getName();
            switch (column.getConceptType()) {
                case SingleSelect:
                case MultiSelect: {
                    return String.format("public.get_coded_string_value(%s->'%s', %s.map)::TEXT as \"%s\"",
                            obsColumn, conceptUUID, conceptMapName, column.getName());
                }
                case Date:
                case DateTime: {
                    return String.format("(%s->>'%s')::DATE as \"%s\"", obsColumn, conceptUUID, columnName);
                }
                case Numeric: {
                    return String.format("(%s->>'%s')::NUMERIC as \"%s\"", obsColumn, conceptUUID, columnName);
                }
                case Subject:
                case Location: {
                    return String.format("(%s->>'%s') as \"%s\"", obsColumn, conceptUUID, columnName);
                }
                case PhoneNumber: {
                    String phoneNumber = String.format("jsonb_extract_path_text(%s->'%s', 'phoneNumber') as \"%s\"", obsColumn, conceptUUID, columnName);
                    String verified = String.format("jsonb_extract_path_text(%s->'%s', 'verified') as \"%s\"", obsColumn, conceptUUID, columnName.concat(" Verified"));
                    return phoneNumber.concat(",\n").concat(verified);
                }
                default: {
                    return String.format("(%s->>'%s')::TEXT as \"%s\"", obsColumn, conceptUUID, columnName);
                }
            }
        }).collect(Collectors.joining(",\n"));
        return "," + columnSelects;
    }

}
