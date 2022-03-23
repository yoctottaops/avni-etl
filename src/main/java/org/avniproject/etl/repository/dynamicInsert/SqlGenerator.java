package org.avniproject.etl.repository.dynamicInsert;

import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.metadata.ColumnMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import static org.avniproject.etl.domain.metadata.diff.Strings.COMMA;

public class SqlGenerator {
    public String generateSql(TableMetadata tableMetadata) throws IOException {
        switch (tableMetadata.getType()) {
            case Person: {
                String template = new BufferedReader(new InputStreamReader(new ClassPathResource("/pivot/registration.sql").getInputStream()))
                        .lines()
                        .collect(Collectors.joining("\n"));
                return template.replace("${schema_name}", ContextHolder.getDbSchema())
                        .replace("${table_name}", tableMetadata.getName())
                        .replace("${observations_to_insert_list}", getListOfObservations(tableMetadata))
                        .replace("${concept_maps}", getConceptMaps(tableMetadata))
                        .replace("${cross_join_concept_maps}", "cross join " + getConceptMapName(tableMetadata))
                        .replace("${operationalSubjectTypeUuid}", tableMetadata.getSubjectTypeId().toString())
                        .replace("${selections}", tableMetadata.getSubjectTypeId().toString());
            }
            default:
        }
        return null;
    }

    private String getConceptMapName(TableMetadata tableMetadata) {
        return tableMetadata.getName() + "_" + "concept_maps";
    }

    private String getConceptMaps(TableMetadata tableMetadata) throws IOException {
        String template = new BufferedReader(new InputStreamReader(new ClassPathResource("/pivot/conceptMap.sql").getInputStream()))
                .lines()
                .collect(Collectors.joining("\n"));
        List<String> names = tableMetadata.getNonDefaultColumnMetadataList().stream().map(columnMetadata -> wrapInQuotes(columnMetadata.getConceptUuid())).collect(Collectors.toList());

        return template
                .replace("${mapName}", getConceptMapName(tableMetadata))
                .replace("${conceptUuids}", String.join(", ", names));
    }

    private String getListOfObservations(TableMetadata tableMetadata) {
        StringBuffer list = new StringBuffer();
        if (tableMetadata.hasNonDefaultColumns()) {
            list.append(COMMA);
        }
        List<String> names = tableMetadata.getNonDefaultColumnMetadataList().stream().map(columnMetadata -> wrapInQuotes(columnMetadata.getName())).collect(Collectors.toList());
        String columnNames = String.join(", ", names);

        return list.append(columnNames).toString();
    }

    private String wrapInQuotes(String name) {
        return "\"" + name + "\"";
    }

//    private String buildObservationSelection(String entity, List<ColumnMetadata> elements, boolean spreadMultiSelectObs) {
//        return buildObservationSelection(entity, elements, spreadMultiSelectObs, "observations");
//    }
//
//    private String buildCancelObservationSelection(String entity, List<ColumnMetadata> elements, boolean spreadMultiSelectObs) {
//        return buildObservationSelection(entity, elements, spreadMultiSelectObs, "cancel_observations");
//    }
//
//    private String buildExitObservationSelection(List<ColumnMetadata> elements) {
//        return buildObservationSelection("programEnrolment", elements, false, "program_exit_observations");
//    }

//    private String buildObservationSelection(String entity, List<ColumnMetadata> elements, Boolean spreadMultiSelectObs, String obsColumnName) {
//        List<ColumnMetadata> viewGenConcepts = elements;
//        String obsColumn = entity + "." + obsColumnName;
//        return viewGenConcepts.parallelStream().map(viewGenConcept -> {
//            Concept concept = viewGenConcept.getConcept();
//            String conceptUUID = viewGenConcept.getConceptUuid();
//            String columnName = viewGenConcept.getName();
//            switch (viewGenConcept.getType()) {
//                case Coded: {
//                    if (spreadMultiSelectObs) {
//                        return spreadMultiSelectSQL(obsColumn, concept);
//                    }
//                    return String.format("public.get_coded_string_value(%s->'%s', %s.map)::TEXT as \"%s\"",
//                            obsColumn, conceptUUID, viewGenConcept.getConceptMapName(), columnName);
//                }
//                case Date:
//                case DateTime: {
//                    return String.format("(%s->>'%s')::DATE as \"%s\"", obsColumn, conceptUUID, columnName);
//                }
//                case Numeric: {
//                    return String.format("(%s->>'%s')::NUMERIC as \"%s\"", obsColumn, conceptUUID, columnName);
//                }
//                case Subject:
//                case Location: {
//                    return String.format("(%s->>'%s') as \"%s\"", obsColumn, conceptUUID, columnName);
//                }
//                case PhoneNumber: {
//                    String phoneNumber = String.format("jsonb_extract_path_text(%s->'%s', 'phoneNumber') as \"%s\"", obsColumn, conceptUUID, columnName);
//                    String verified = String.format("jsonb_extract_path_text(%s->'%s', 'verified') as \"%s\"", obsColumn, conceptUUID, columnName.concat(" Verified"));
//                    return phoneNumber.concat(",\n").concat(verified);
//                }
//                default: {
//                    return String.format("(%s->>'%s')::TEXT as \"%s\"", obsColumn, conceptUUID, columnName);
//                }
//            }
//        }).collect(Collectors.joining(",\n"));
//    }

//    private boolean skipConcept(List<FormElement> elements, ViewGenConcept viewGenConcept) {
//        return viewGenConcept.isDecisionConcept() && elements.stream().anyMatch(formElement -> formElement.getConcept().getName().equals(viewGenConcept.getConcept().getName()));
//    }
//
//    private String spreadMultiSelectSQL(String obsColumn, Concept concept) {
//        String obsSubColumn = String.format("(%s->'%s')", obsColumn, concept.getUuid());
//        return concept.getConceptAnswers().stream().map(ConceptAnswer::getAnswerConcept)
//                .map(aConcept -> String.format("boolean_txt(%s ? '%s') as \"%s(%s)\"",
//                        obsSubColumn, aConcept.getUuid(), aConcept.getName(), concept.getName()))
//                .collect(Collectors.joining(",\n"));
//    }

}
