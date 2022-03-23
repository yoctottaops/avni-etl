package org.avniproject.etl.repository;

import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.metadata.SchemaMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.avniproject.etl.domain.metadata.diff.Diff;
import org.avniproject.etl.repository.rowMappers.TableMetadataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.avniproject.etl.repository.JdbcContextWrapper.runInOrgContext;

@Repository
public class SchemaMetadataRepository {
    private final JdbcTemplate jdbcTemplate;

    private final ColumnMetadataRepository columnMetadataRepository;

    @Autowired
    public SchemaMetadataRepository(JdbcTemplate jdbcTemplate, ColumnMetadataRepository columnMetadataRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.columnMetadataRepository = columnMetadataRepository;
    }

    public SchemaMetadata getNewSchemaMetadata() {
        String sql = "select fm.id                                                                  form_mapping_id,\n" +
                "       f.id                                                                   form_id,\n" +
                "       ost.name                                                               subject_type_name,\n" +
                "       st.id                                                                  subject_type_id,\n" +
                "       st.type                                                                subject_type_type,\n" +
                "       f.form_type                                                            table_type,\n" +
                "       p.id                                                                   program_id,\n" +
                "       op.name                                                                program_name,\n" +
                "       et.id                                                                  encounter_type_id,\n" +
                "       oet.name                                                               encounter_type_name,\n" +
                "       fm.enable_approval                                                     enable_approval,\n" +
                "       c.name                                                                 concept_name,\n" +
                "       c.id                                                                   concept_id,\n" +
                "       (case when c.data_type = 'Coded' then fe.type else c.data_type end) as element_type\n" +
                "from form_mapping fm\n" +
                "         inner join form f on fm.form_id = f.id\n" +
                "         left outer join form_element_group feg on f.id = feg.form_id\n" +
                "         left outer join form_element fe on feg.id = fe.form_element_group_id\n" +
                "         left outer join concept c on fe.concept_id = c.id\n" +
                "         inner join subject_type st on fm.subject_type_id = st.id\n" +
                "         inner join operational_subject_type ost on st.id = ost.subject_type_id\n" +
                "         left outer join program p on fm.entity_id = p.id\n" +
                "         left outer join operational_program op on p.id = op.program_id\n" +
                "         left outer join encounter_type et on fm.observations_type_entity_id = et.id\n" +
                "         left outer join operational_encounter_type oet on et.id = oet.encounter_type_id\n" +
                "where fm.is_voided is false;";

        List<Map<String, Object>> maps = runInOrgContext(() -> jdbcTemplate.queryForList(sql), jdbcTemplate);

        Map<Object, List<Map<String, Object>>> tableMaps = maps.stream().collect(Collectors.groupingBy(stringObjectMap -> stringObjectMap.get("form_mapping_id")));
        List<TableMetadata> tables = tableMaps.values().stream().map(mapList -> new TableMetadataMapper().create(mapList)).collect(Collectors.toList());

        return new SchemaMetadata(tables);
    }

    public SchemaMetadata getExistingSchemaMetadata() {
        String sql = "select tm.id                table_id,\n" +
                "       tm.db_user           db_user,\n" +
                "       tm.type              table_type,\n" +
                "       tm.name              table_name,\n" +
                "       tm.form_id           form_id,\n" +
                "       tm.encounter_type_id encounter_type_id,\n" +
                "       tm.program_id        program_id,\n" +
                "       tm.subject_type_id   subject_type_id,\n" +
                "       cm.id                column_id,\n" +
                "       cm.type              column_type,\n" +
                "       cm.concept_id        concept_id,\n" +
                "       cm.name              concept_name,\n" +
                "       cm.concept_uuid      concept_uuid\n" +
                "from table_metadata tm\n" +
                "         left outer join column_metadata cm on tm.id = cm.table_id;";

        List<Map<String, Object>> maps = runInOrgContext(() -> jdbcTemplate.queryForList(sql), jdbcTemplate);
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
                        .map(this::save)
                        .collect(Collectors.toList()));
    }

    public TableMetadata save(TableMetadata tableMetadata) {
        return columnMetadataRepository.saveColumns(
                saveTable(tableMetadata));
    }

    private TableMetadata saveTable(TableMetadata tableMetadata) {
        return tableMetadata.getId() == null ?
                insert(tableMetadata) :
                update(tableMetadata);
    }

    public TableMetadata update(TableMetadata tableMetadata) {
        String sql = "update table_metadata\n" +
                "set name              = :name,\n" +
                "    type              = :type,\n" +
                "    subject_type_id   = :subject_type_id,\n" +
                "    program_id        = :program_id,\n" +
                "    encounter_type_id = :encounter_type_id,\n" +
                "    form_id           = :form_id\n" +
                "where id = :id;";

        new NamedParameterJdbcTemplate(jdbcTemplate).update(sql, addParameters(tableMetadata));
        return tableMetadata;
    }

    public TableMetadata insert(TableMetadata tableMetadata) {
        Number id = new SimpleJdbcInsert(jdbcTemplate).withTableName("table_metadata")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKey(addParameters(tableMetadata));

        tableMetadata.setId(id.intValue());
        return tableMetadata;
    }

    private Map<String, Object> addParameters(TableMetadata tableMetadata) {
        Map<String, Object> parameters = new HashMap<>(1);
        parameters.put("id", tableMetadata.getId());
        parameters.put("db_user", ContextHolder.getDbUser());
        parameters.put("name", tableMetadata.getName());
        parameters.put("type", tableMetadata.getType().toString());
        parameters.put("subject_type_id", tableMetadata.getSubjectTypeId());
        parameters.put("program_id", tableMetadata.getProgramId());
        parameters.put("encounter_type_id", tableMetadata.getEncounterTypeId());
        parameters.put("form_id", tableMetadata.getFormId());

        return parameters;
    }
}
