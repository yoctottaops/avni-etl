package org.avniproject.etl.repository.rowMappers;

import org.avniproject.etl.domain.metadata.Column;
import org.avniproject.etl.domain.metadata.ColumnMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.avniproject.etl.repository.rowMappers.tableMappers.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TableMetadataMapper {
    public TableMetadata createFromExistingSchema(List<Map<String, Object>> columns) {
        TableMetadata tableMetadata = new TableMetadata();
        Map<String, Object> tableDetails = columns.get(0);
        populateCommonColumns(tableMetadata, tableDetails);

        tableMetadata.setName((String) tableDetails.get("table_name"));
        tableMetadata.setId((Integer) tableDetails.get("table_id"));

        tableMetadata.addColumnMetadata(columns.stream()
                .filter(stringObjectMap -> stringObjectMap.get("column_id") != null)
                .map(
                        column -> new ColumnMetadata(
                                (Integer) column.get("column_id"),
                                new Column((String) column.get("concept_name"),
                                        Column.Type.valueOf((String) column.get("column_type"))),
                                (Integer) column.get("concept_id")))
                .collect(Collectors.toList()));

        return tableMetadata;
    }

    public TableMetadata create(List<Map<String, Object>> columns) {
        TableMetadata tableMetadata = new TableMetadata();
        Map<String, Object> tableDetails = columns.get(0);
        tableMetadata.setFormId(((Integer) tableDetails.get("form_id")));
        tableMetadata.setType(TableMetadata.Type.valueOf((String) tableDetails.get("table_type")));
        tableMetadata.setSubjectTypeId((Integer) tableDetails.get("subject_type_id"));
        tableMetadata.setEncounterTypeId((Integer) tableDetails.get("encounter_type_id"));
        TableStructure table = getTableStructure(tableDetails);
        tableMetadata.setName(table.name(tableDetails));
        tableMetadata.setProgramId((Integer) tableDetails.get("program_id"));

        tableMetadata.addColumnMetadata(table.columns().stream().map(column -> new ColumnMetadata(new Column(column.getName(), column.getType()), null)).collect(Collectors.toList()));
        tableMetadata.addColumnMetadata(columns.stream()
                .filter(stringObjectMap -> stringObjectMap.get("concept_id") != null)
                .map(
                        column -> new ColumnMetadata(new Column(
                                (String) column.get("concept_name"),
                                (ConceptDatatypeMapper.map((String) column.get("element_type")))),
                                (Integer) column.get("concept_id"))).collect(Collectors.toList()));

        return tableMetadata;
    }

    private void populateCommonColumns(TableMetadata tableMetadata, Map<String, Object> tableDetails) {
        tableMetadata.setFormId(((Integer) tableDetails.get("form_id")));
        tableMetadata.setType(TableMetadata.Type.valueOf((String) tableDetails.get("table_type")));
        tableMetadata.setSubjectTypeId((Integer) tableDetails.get("subject_type_id"));
        tableMetadata.setEncounterTypeId((Integer) tableDetails.get("encounter_type_id"));
        tableMetadata.setProgramId((Integer) tableDetails.get("program_id"));
    }

    public TableStructure getTableStructure(Map<String, Object> tableDetails) {
        switch (TableMetadata.Type.valueOf((String) tableDetails.get("table_type"))) {
            case Group:
            case IndividualProfile:
            case Household:
                return new SubjectTable();
            case Person:
                return new PersonTable();
            case ProgramEnrolment:
                return new ProgramEnrolmentTable();
            case ProgramExit:
                return new ProgramExitTable();
            case ProgramEncounter:
                return new ProgramEncounterTable();
            case ProgramEncounterCancellation:
                return new ProgramEncounterCancellationTable();
            case Encounter:
                return new EncounterTable();
            case EncounterCancellation:
                return new EncounterCancellationTable();
            default:
                throw new RuntimeException("Cannot create name for table details" + tableDetails);
        }
    }
}
