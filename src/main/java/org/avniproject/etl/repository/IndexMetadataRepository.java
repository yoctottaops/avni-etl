package org.avniproject.etl.repository;

import org.avniproject.etl.domain.metadata.IndexMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class IndexMetadataRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public IndexMetadataRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<IndexMetadata> save(TableMetadata tableMetadata) {
        return tableMetadata
                .getIndexMetadataList()
                .stream()
                .map(indexMetadata -> indexMetadata.getId() == null ? insert(tableMetadata.getId(), indexMetadata): indexMetadata)
                .collect(Collectors.toList());
    }

    private IndexMetadata insert(Integer tableId, IndexMetadata indexMetadata) {
        Number id = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("index_metadata")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKey(addParameters(tableId, indexMetadata));
        indexMetadata.setId(id.intValue());

        return indexMetadata;
    }

    private Map<String, Object> addParameters(Integer tableId, IndexMetadata indexMetadata) {
        Map<String, Object> parameters = new HashMap<>(1);
        parameters.put("name", indexMetadata.getName());
        parameters.put("table_metadata_id", tableId);
        parameters.put("column_id", indexMetadata.getColumnId());
        return parameters;
    }
}

