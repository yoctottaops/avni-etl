package org.avniproject.etl.repository.sync;

import org.avniproject.etl.domain.NullObject;
import org.avniproject.etl.domain.OrgIdentityContextHolder;
import org.avniproject.etl.domain.metadata.SchemaMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;

import static org.avniproject.etl.repository.JdbcContextWrapper.runInOrgContext;

@Repository
public class CleanEnrolmentExitTableAction implements EntitySyncAction {
    private final JdbcTemplate jdbcTemplate;

    private static final String deleteInvalidExitsSql = "delete from \"${schemaName}\".\"${exitTableName}\"\n" +
            "using \"${schemaName}\".\"${primaryTableName}\"\n" +
            "where \"${schemaName}\".\"${exitTableName}\".id = \"${schemaName}\".\"${primaryTableName}\".id\n" +
            "and \"${schemaName}\".\"${primaryTableName}\".program_exit_date_time is null;";
    @Autowired
    public CleanEnrolmentExitTableAction(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void perform(TableMetadata tableMetadata, Date lastSyncTime, Date dataSyncBoundaryTime, SchemaMetadata currentSchemaMetadata) {
        if (this.doesntSupport(tableMetadata)) {
            return;
        }
        cleanInvalidExits(tableMetadata);
    }

    private void cleanInvalidExits(TableMetadata tableMetadata) {
        String schema = OrgIdentityContextHolder.getDbSchema();
        String exitTableName = tableMetadata.getName();
        String primaryTableName = exitTableName.substring(0, exitTableName.length() - 5);
        String sql = deleteInvalidExitsSql
                .replace("${schemaName}", schema)
                .replace("${primaryTableName}", primaryTableName)
                .replace("${exitTableName}", exitTableName);
        runInOrgContext(() -> {
            jdbcTemplate.execute(sql);
            return NullObject.instance();
        }, jdbcTemplate);
    }

    private boolean supports(TableMetadata tableMetadata) {
        return tableMetadata.getType().equals(TableMetadata.Type.ProgramExit);
    }

    public boolean doesntSupport(TableMetadata tableMetadata) {
        return !supports(tableMetadata);
    }

}
