package org.avniproject.etl.repository.rowMappers;

import org.avniproject.etl.domain.metadata.Column;
import org.avniproject.etl.domain.metadata.ColumnMetadata;
import org.avniproject.etl.domain.metadata.IndexMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.avniproject.etl.repository.rowMappers.tableMappers.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TableMetadataMapper {
    public TableMetadata createFromExistingSchema(List<Map<String, Object>> columns, List<Map<String, Object>> indices) {
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
                                new Column(
                                        (String) column.get("concept_name"),
                                        Column.Type.valueOf((String) column.get("column_type"))
                                ),
                                (Integer) column.get("concept_id"),
                                column.get("concept_type") == null ? null : ColumnMetadata.ConceptType.valueOf((String) column.get("concept_type")),
                                (String) column.get("concept_uuid"),
                                null))
                .collect(Collectors.toList()));

        tableMetadata.addIndexMetadata(indices.stream().map(index -> new IndexMetadata(
                (Integer) index.get("index_id"),
                (String) index.get("index_name"),
                new ColumnMetadata(
                        (Integer) index.get("column_id"),
                        new Column(
                                (String) index.get("concept_name"),
                                Column.Type.valueOf((String) index.get("column_type"))
                        ),
                        (Integer) index.get("concept_id"),
                        index.get("concept_type") == null ? null : ColumnMetadata.ConceptType.valueOf((String) index.get("concept_type")),
                        (String) index.get("concept_uuid"),
                        null)
        )).collect(Collectors.toList()));

        return tableMetadata;
    }

    public TableMetadata create(List<Map<String, Object>> columns) {
        TableMetadata tableMetadata = new TableMetadata();
        Map<String, Object> tableDetails = columns.get(0);
        populateCommonColumns(tableMetadata, tableDetails);
        Table table = getTableStructure(tableMetadata.getType(), tableDetails);
        tableMetadata.setName(table.name(tableDetails));

        tableMetadata.addColumnMetadata(table.columns().stream().map(column -> new ColumnMetadata(column, null, null, null)).collect(Collectors.toList()));
        tableMetadata.addColumnMetadata(columns.stream()
                .filter(stringObjectMap -> stringObjectMap.get("concept_id") != null)
                .map(column -> new ColumnMetadataMapper().create(column)).collect(Collectors.toList()));

        table.columns().forEach(column -> {
            if (column.isIndexed()) {
                tableMetadata.addIndexMetadata(column);
            }
        });

        return tableMetadata;
    }

    private void populateCommonColumns(TableMetadata tableMetadata, Map<String, Object> tableDetails) {
        tableMetadata.setFormUuid(((String) tableDetails.get("form_uuid")));
        tableMetadata.setType(getTableType(tableDetails));
        tableMetadata.setSubjectTypeUuid((String) tableDetails.get("subject_type_uuid"));
        tableMetadata.setEncounterTypeUuid((String) tableDetails.get("encounter_type_uuid"));
        tableMetadata.setProgramUuid((String) tableDetails.get("program_uuid"));
    }

    private TableMetadata.Type getTableType(Map<String, Object> tableDetails) {
        String tableType = (String) tableDetails.get("table_type");
        return tableType.equals("IndividualProfile") ?
                TableMetadata.Type.valueOf((String) tableDetails.get("subject_type_type")) :
                TableMetadata.Type.valueOf(tableType);
    }

    public Table getTableStructure(TableMetadata.Type type, Map<String, Object> tableDetails) {
        switch (type) {
            case Group:
            case Household:
            case Individual:
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
            case IndividualEncounterCancellation:
                return new EncounterCancellationTable();
            default:
                throw new RuntimeException("Cannot create name for table details" + tableDetails);
        }
    }
}
