package org.avniproject.etl.repository;

import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.avniproject.etl.repository.dynamicInsert.SqlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;

import static org.avniproject.etl.repository.JdbcContextWrapper.runInOrgContext;

@Repository
public class EntityRepository {
    private JdbcTemplate jdbcTemplate;


    @Autowired
    public EntityRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveEntities(TableMetadata tableMetadata, Date lastSyncTime, Date dataSyncBoundaryTime) {
        System.out.println(new SqlGenerator().generateSql(tableMetadata, lastSyncTime, dataSyncBoundaryTime));
        jdbcTemplate.execute(new SqlGenerator().generateSql(tableMetadata, lastSyncTime, dataSyncBoundaryTime));
        deleteDuplicateRows(tableMetadata.getName());
    }

    public void deleteDuplicateRows(String tableName) {
        String schema = ContextHolder.getDbSchema();
        String baseSql = "delete from \"${schemaName}\".\"${tableName}\"\n" +
                "where id in (\n" +
                "    select t1.id\n" +
                "    from \"${schemaName}\".\"${tableName}\" t1\n" +
                "             inner join \"${schemaName}\".\"${tableName}\" t2 on\n" +
                "                t1.id = t2.id\n" +
                "            and t1.last_modified_date_time < t2.last_modified_date_time);";

        String sql = baseSql
                .replace("${schemaName}", schema)
                .replace("${tableName}", tableName);
        runInOrgContext(() -> {
            jdbcTemplate.execute(sql);
            return 0;
        }, jdbcTemplate);
    }
}
