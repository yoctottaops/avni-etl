package org.avniproject.etl.repository;

import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.metadata.Column;
import org.avniproject.etl.domain.metadata.ColumnMetadata;
import org.avniproject.etl.domain.metadata.SchemaMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.avniproject.etl.domain.metadata.diff.Diff;
import org.avniproject.etl.repository.rowMappers.ColumnMetadataMapper;
import org.avniproject.etl.repository.rowMappers.TableMetadataMapper;
import org.avniproject.etl.repository.rowMappers.tableMappers.CommonColumns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.avniproject.etl.repository.JdbcContextWrapper.runInOrgContext;

@Repository
public class SchemaMetadataRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final String PLACEHOLDER_CONCEPT_UUID = "b4e5a662-97bf-4846-b9b7-9baeab4d89c4";
    private final TableMetadataRepository tableMetadataRepository;

    @Autowired
    public SchemaMetadataRepository(JdbcTemplate jdbcTemplate, TableMetadataRepository tableMetadataRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.tableMetadataRepository = tableMetadataRepository;
    }

    @Transactional(readOnly = true)
    public SchemaMetadata getNewSchemaMetadata() {
        List<TableMetadata> tables = new ArrayList<>();
        tables.addAll(getFormTables());
        tables.add(getAddressTable());

        return new SchemaMetadata(tables);
    }

    private TableMetadata getAddressTable() {
        List<Map<String, Object>> addressLevelTypes = runInOrgContext(() -> jdbcTemplate.queryForList("select name from address_level_type;"), jdbcTemplate);
        List<Column> columns = addressLevelTypes
                .stream()
                .map(addressLevelTypeMap -> new Column((String) addressLevelTypeMap.get("name"), Column.Type.text))
                .collect(Collectors.toList());
        columns.addAll(CommonColumns.commonColumns);
        List<ColumnMetadata> columnMetadataList = columns.stream()
                .map(column -> new ColumnMetadata(column, null, null, null))
                .collect(Collectors.toList());
        TableMetadata tableMetadata = new TableMetadata();
        tableMetadata.setName("address");
        tableMetadata.setType(TableMetadata.Type.Address);
        tableMetadata.setColumnMetadataList(columnMetadataList);

        return tableMetadata;
    }

    private List<TableMetadata> getFormTables() {
        String sql = format("select fm.uuid                                                           form_mapping_uuid,\n" +
                "       f.uuid                                                                 form_uuid,\n" +
                "       ost.name                                                               subject_type_name,\n" +
                "       st.uuid                                                                subject_type_uuid,\n" +
                "       st.type                                                                subject_type_type,\n" +
                "       f.form_type                                                            table_type,\n" +
                "       p.uuid                                                                 program_uuid,\n" +
                "       op.name                                                                program_name,\n" +
                "       et.uuid                                                                encounter_type_uuid,\n" +
                "       oet.name                                                               encounter_type_name,\n" +
                "       fm.enable_approval                                                     enable_approval,\n" +
                "       c.name                                                                 concept_name,\n" +
                "       gc.name                                                                parent_concept_name,\n" +
                "       c.id                                                                   concept_id,\n" +
                "       gc.id                                                                  parent_concept_id,\n" +
                "       c.uuid                                                                 concept_uuid,\n" +
                "       gc.uuid                                                                parent_concept_uuid,\n" +
                "       (case when c.data_type = 'Coded' then fe.type else c.data_type end) as element_type,\n" +
                "       (case when gc.data_type = 'Coded' then gfe.type else gc.data_type end) as parent_element_type\n" +
                "from form_mapping fm\n" +
                "         inner join form f on fm.form_id = f.id\n" +
                "         left outer join form_element_group feg on f.id = feg.form_id\n" +
                "         left outer join form_element fe on feg.id = fe.form_element_group_id\n and fe.is_voided is false" +
                "         left outer join form_element gfe on gfe.id = fe.group_id\n and gfe.is_voided is false" +
                "         left outer join concept c on fe.concept_id = c.id and c.data_type <> 'QuestionGroup'\n" +
                "         left outer join concept gc on gfe.concept_id = gc.id\n" +
                "         inner join subject_type st on fm.subject_type_id = st.id\n" +
                "         inner join operational_subject_type ost on st.id = ost.subject_type_id\n" +
                "         left outer join program p on fm.entity_id = p.id\n" +
                "         left outer join operational_program op on p.id = op.program_id\n" +
                "         left outer join encounter_type et on fm.observations_type_entity_id = et.id\n" +
                "         left outer join operational_encounter_type oet on et.id = oet.encounter_type_id\n" +
                "where fm.is_voided is false and c.uuid <> '%s';", PLACEHOLDER_CONCEPT_UUID);

        List<Map<String, Object>> maps = runInOrgContext(() -> jdbcTemplate.queryForList(sql), jdbcTemplate);

        Map<Object, List<Map<String, Object>>> tableMaps = maps.stream().collect(Collectors.groupingBy(stringObjectMap -> stringObjectMap.get("form_mapping_uuid")));
        List<TableMetadata> tables = tableMaps.values().stream().map(mapList -> new TableMetadataMapper().create(mapList)).collect(Collectors.toList());
        tables.forEach(this::addDecisionConceptColumns);
        return tables;
    }

    private void addDecisionConceptColumns(TableMetadata tableMetadata) {
        String sql = "select c.id                                                                      as concept_id,\n" +
                "       c.uuid                                                                    as concept_uuid,\n" +
                "       c.name                                                                    as concept_name,\n" +
                "       (case when c.data_type = 'Coded' then 'MultiSelect' else c.data_type end) as element_type\n" +
                "from decision_concept dc\n" +
                "         inner join concept c on dc.concept_id = c.id\n" +
                "         inner join form f on f.id = dc.form_id\n" +
                "where f.uuid = ?;";

        List<Map<String, Object>> conceptIds = runInOrgContext(() -> jdbcTemplate.queryForList(sql, tableMetadata.getFormUuid()), jdbcTemplate);

        tableMetadata.addColumnMetadata(
                new ArrayList<>(conceptIds
                        .stream()
                        .map(column -> new ColumnMetadataMapper().create(column))
                        .collect(Collectors.toMap(
                                ColumnMetadata::getName,
                                obj -> obj,
                                (first, second) -> first
                        )).values()));
    }

    @Transactional(readOnly = true)
    public SchemaMetadata getExistingSchemaMetadata() {
        String sql = format("select tm.id                table_id,\n" +
                "       tm.type                                 table_type,\n" +
                "       tm.name                                 table_name,\n" +
                "       tm.form_uuid                            form_uuid,\n" +
                "       tm.encounter_type_uuid                  encounter_type_uuid,\n" +
                "       tm.program_uuid                         program_uuid,\n" +
                "       tm.subject_type_uuid                    subject_type_uuid,\n" +
                "       cm.id                                   column_id,\n" +
                "       cm.type                                 column_type,\n" +
                "       cm.concept_type                         concept_type,\n" +
                "       cm.concept_id                           concept_id,\n" +
                "       cm.name                                 concept_name,\n" +
                "       cm.concept_uuid                         concept_uuid\n" +
                "from table_metadata tm\n" +
                "         left outer join column_metadata cm on tm.id = cm.table_id\n" +
                "     where tm.schema_name = '%s';", ContextHolder.getDbSchema());

        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        Map<Object, List<Map<String, Object>>> tableMaps = maps.stream().collect(Collectors.groupingBy(stringObjectMap -> stringObjectMap.get("table_id")));
        List<TableMetadata> tables = tableMaps.values().stream().map(mapList -> new TableMetadataMapper().createFromExistingSchema(mapList)).collect(Collectors.toList());
        return new SchemaMetadata(tables);
    }

    public void applyChanges(List<Diff> changes) {
        changes.forEach(change -> jdbcTemplate.execute(change.getSql()));
    }

    public void save(SchemaMetadata schemaMetadata) {
        schemaMetadata.setTableMetadata(
                schemaMetadata.getTableMetadata()
                        .stream()
                        .map(tableMetadataRepository::save)
                        .collect(Collectors.toList()));
    }
}
