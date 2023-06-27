package org.avniproject.etl.repository.sync;

import org.avniproject.etl.domain.NullObject;
import org.avniproject.etl.domain.metadata.SchemaMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.avniproject.etl.repository.sql.TransactionalSyncSqlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;

import static org.avniproject.etl.repository.JdbcContextWrapper.runInOrgContext;

@Repository
public class TransactionalTablesSyncAction implements EntitySyncAction {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TransactionalTablesSyncAction(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean doesntSupport(TableMetadata tableMetadata) {
        return !new TransactionalSyncSqlGenerator().supports(tableMetadata);
    }

    @Override
    public void perform(TableMetadata tableMetadata, Date lastSyncTime, Date dataSyncBoundaryTime, SchemaMetadata currentSchemaMetadata) {
        if (this.doesntSupport(tableMetadata)) {
            return;
        }
        runInOrgContext(() -> {
            jdbcTemplate.execute(new TransactionalSyncSqlGenerator().generateSql(tableMetadata, lastSyncTime, dataSyncBoundaryTime));
            return NullObject.instance();
        }, jdbcTemplate);
    }
}
