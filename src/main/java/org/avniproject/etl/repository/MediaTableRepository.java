package org.avniproject.etl.repository;

import org.avniproject.etl.config.ContextHolderUtil;
import org.avniproject.etl.dto.MediaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.avniproject.etl.repository.JdbcContextWrapper.runInOrgContext;

@Repository
public class MediaTableRepository {

    private final JdbcTemplate jdbcTemplate;

    private final ContextHolderUtil contextHolderUtil;

    @Autowired
    MediaTableRepository(JdbcTemplate jdbcTemplate, ContextHolderUtil contextHolderUtil){
        this.jdbcTemplate = jdbcTemplate;
        this.contextHolderUtil = contextHolderUtil;
    }


    public List<MediaDTO> findAll(int size, int page) {
        String schemaName = contextHolderUtil.getSchemaName();
        Map<String, Object> parameters = contextHolderUtil.getParameters();

        String sql = "SELECT " + schemaName + ".media.*, row_to_json(" + schemaName + ".address.*) as address FROM " + schemaName + ".media JOIN " + schemaName + ".address ON " + schemaName + ".address.id=" + schemaName + ".media.address_id where " + schemaName + ".media.image_url is not null LIMIT " + size + " OFFSET " + size * page;

        return runInOrgContext(
                () -> new NamedParameterJdbcTemplate(jdbcTemplate)
                .query(sql, parameters, (rs, rowNum) -> setMediaDto(rs)), jdbcTemplate);
    }

    private MediaDTO setMediaDto(ResultSet rs)  throws SQLException {

        String uuid = rs.getString("uuid");
        String imageUrl = rs.getString("image_url");
        String thumbnailUrl = imageUrl;
        String subjectTypeName = rs.getString("subject_type_name");
        String programName = rs.getString("program_name");
        String encounterTypeName = rs.getString("encounter_type_name");
        String lastModifiedDateTime = rs.getString("last_modified_date_time");
        String createdDateTime = rs.getString("created_date_time");
        String syncParameterKey1 = rs.getString("sync_parameter_key1");
        String syncParameterKey2 = rs.getString("sync_parameter_key2");
        String syncParameterValue1 = rs.getString("sync_parameter_value1");
        String syncParameterValue2 = rs.getString("sync_parameter_value2");
        Object address = rs.getString("address");

        return new MediaDTO(
                uuid,
                imageUrl,
                thumbnailUrl,
                subjectTypeName,
                programName,
                encounterTypeName,
                lastModifiedDateTime,
                createdDateTime,
                syncParameterKey1,
                syncParameterKey2,
                syncParameterValue1,
                syncParameterValue2,
                address
        );
    }

    public int findTotalMedia() throws DataAccessException {
        String schemaName = contextHolderUtil.getSchemaName();

        String sql = "SELECT count(*) as record_count FROM " + schemaName + ".media JOIN " + schemaName + ".address ON " + schemaName + ".address.id=" + schemaName + ".media.address_id where " + schemaName + ".media.image_url is not null";

        return runInOrgContext(() -> jdbcTemplate.queryForObject(sql, Integer.class), jdbcTemplate);
    }
}
