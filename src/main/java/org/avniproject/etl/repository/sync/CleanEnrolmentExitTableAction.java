package org.avniproject.etl.repository.sync;

import org.avniproject.etl.domain.NullObject;
import org.avniproject.etl.domain.OrgIdentityContextHolder;
import org.avniproject.etl.domain.metadata.SchemaMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.avniproject.etl.repository.sql.SqlFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.stringtemplate.v4.ST;

import java.util.Date;

import static org.avniproject.etl.repository.JdbcContextWrapper.runInOrgContext;

@Repository
public class CleanEnrolmentExitTableAction implements EntitySyncAction {
    private final JdbcTemplate jdbcTemplate;
    private static final String deleteInvalidExitsSqlTemplate = SqlFile.readSqlFile("deleteInvalidExits.sql.st");
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
        String sql = new ST(deleteInvalidExitsSqlTemplate)
                .add("schemaName", wrapInQuotes(schema))
                .add("exitTableName", wrapInQuotes(exitTableName))
                .add("primaryTableName", wrapInQuotes(primaryTableName))
                .render();
        runInOrgContext(() -> {
            jdbcTemplate.execute(sql);
            return NullObject.instance();
        }, jdbcTemplate);
    }

    private String wrapInQuotes(String parameter) {
        return parameter == null ? "null" : "\"" + parameter + "\"";
    }

    private boolean supports(TableMetadata tableMetadata) {
        return tableMetadata.getType().equals(TableMetadata.Type.ProgramExit);
    }

    public boolean doesntSupport(TableMetadata tableMetadata) {
        return !supports(tableMetadata);
    }

}
