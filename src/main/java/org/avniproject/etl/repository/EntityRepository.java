package org.avniproject.etl.repository;

import org.avniproject.etl.domain.metadata.TableMetadata;
import org.avniproject.etl.repository.dynamicInsert.SqlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class EntityRepository {
    private JdbcTemplate jdbcTemplate;


    @Autowired
    public EntityRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveEntities(TableMetadata tableMetadata, Date lastSyncTime, Date dataSyncBoundaryTime) {
        jdbcTemplate.execute(new SqlGenerator().generateSql(tableMetadata, lastSyncTime, dataSyncBoundaryTime));
    }
}
