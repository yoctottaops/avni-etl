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
    private IndexMetadataRepository indexMetadataRepository;

    @Autowired
    public TableMetadataRepository(JdbcTemplate jdbcTemplate, ColumnMetadataRepository columnMetadataRepository, IndexMetadataRepository indexMetadataRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.columnMetadataRepository = columnMetadataRepository;
        this.indexMetadataRepository = indexMetadataRepository;
    }

    public TableMetadata save(TableMetadata tableMetadata) {
        TableMetadata savedTableMetadata = saveTable(tableMetadata);
        savedTableMetadata.setColumnMetadataList(columnMetadataRepository.saveColumns(savedTableMetadata));
        savedTableMetadata.setIndexMetadataList(indexMetadataRepository.save(tableMetadata));

        return savedTableMetadata;
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
                "    subject_type_uuid   = :subject_type_uuid,\n" +
                "    program_uuid        = :program_uuid,\n" +
                "    encounter_type_uuid = :encounter_type_uuid,\n" +
                "    form_uuid           = :form_uuid\n" +
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
        parameters.put("name", tableMetadata.getName());
        parameters.put("type", tableMetadata.getType().toString());
        parameters.put("subject_type_uuid", tableMetadata.getSubjectTypeUuid());
        parameters.put("program_uuid", tableMetadata.getProgramUuid());
        parameters.put("encounter_type_uuid", tableMetadata.getEncounterTypeUuid());
        parameters.put("form_uuid", tableMetadata.getFormUuid());

        return parameters;
    }
}
