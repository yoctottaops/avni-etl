package org.avniproject.etl.repository;

import org.avniproject.etl.domain.syncstatus.EntitySyncStatus;
import org.avniproject.etl.domain.syncstatus.SchemaDataSyncStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EntitySyncStatusRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EntitySyncStatusRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public SchemaDataSyncStatus getSyncStatus() {
        String sql = "select ash.id                as id,\n" +
                "       ash.table_metadata_id as table_metadata_id,\n" +
                "       ash.sync_start_time   as sync_start_time,\n" +
                "       ash.sync_end_time     as sync_end_time,\n" +
                "       ash.sync_status       as sync_status\n" +
                "from entity_sync_status ash;";

        List<EntitySyncStatus> entitySyncStatuses = jdbcTemplate.query(sql, (rs, rowNum) -> new EntitySyncStatus(
                rs.getInt("id"),
                rs.getInt("table_metadata_id"),
                rs.getDate("sync_start_time"),
                rs.getDate("sync_end_time"),
                EntitySyncStatus.Status.valueOf(rs.getString("sync_status"))));

        return new SchemaDataSyncStatus(entitySyncStatuses);
    }
}
