package org.avniproject.etl.repository.sync;

import org.avniproject.etl.domain.NullObject;
import org.avniproject.etl.domain.metadata.SchemaMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.avniproject.etl.repository.sql.RepeatableQuestionGroupSyncSqlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;

import static org.avniproject.etl.repository.JdbcContextWrapper.runInOrgContext;

@Repository
public class RepeatableQGTransactionTablesSyncAction implements EntitySyncAction {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RepeatableQGTransactionTablesSyncAction(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean doesntSupport(TableMetadata tableMetadata) {
        return !new RepeatableQuestionGroupSyncSqlGenerator().supports(tableMetadata);
    }

    @Override
    public void perform(TableMetadata tableMetadata, Date lastSyncTime, Date dataSyncBoundaryTime, SchemaMetadata currentSchemaMetadata) {
        if (this.doesntSupport(tableMetadata)) {
            return;
        }
        runInOrgContext(() -> {
            jdbcTemplate.execute(new RepeatableQuestionGroupSyncSqlGenerator().generateSql(tableMetadata, lastSyncTime, dataSyncBoundaryTime));
            return NullObject.instance();
        }, jdbcTemplate);
    }
}
