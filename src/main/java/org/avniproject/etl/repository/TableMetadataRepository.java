package org.avniproject.etl.repository;

import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class TableMetadataRepository {
    private final JdbcTemplate jdbcTemplate;

    private final ColumnMetadataRepository columnMetadataRepository;

    @Autowired
    public TableMetadataRepository(JdbcTemplate jdbcTemplate, ColumnMetadataRepository columnMetadataRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.columnMetadataRepository = columnMetadataRepository;
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
        parameters.put("schema_name", ContextHolder.getDbSchema());
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
