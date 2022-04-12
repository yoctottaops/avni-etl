package org.avniproject.etl.repository.sync;

import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.NullObject;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.avniproject.etl.repository.dynamicInsert.TransactionalSyncSqlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.avniproject.etl.repository.JdbcContextWrapper.runInOrgContext;

@Repository
public class AnswerConceptSync implements EntitySyncAction {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AnswerConceptSync(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean supports(TableMetadata tableMetadata) {
        return new TransactionalSyncSqlGenerator().supports(tableMetadata);
    }

    @Override
    public void perform(TableMetadata tableMetadata, Date lastSyncTime, Date dataSyncBoundaryTime) {
        if (!this.supports(tableMetadata)) {
            return;
        }
        String query = format("select acm.old_answer_concept_name, acm.new_answer_concept_name, cm.name\n" +
                        "from answer_concept_migration acm\n" +
                        "         join column_metadata cm on acm.concept_id = cm.concept_id\n" +
                        "         join table_metadata tm on cm.table_id = tm.id\n" +
                        "where acm.is_voided = false\n" +
                        "  and tm.id = %d\n" +
                        "  and acm.last_modified_date_time > '%s'\n" +
                        "  and acm.last_modified_date_time <= '%s'\n" +
                        "order by acm.last_modified_date_time asc;",
                tableMetadata.getId(),
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(lastSyncTime),
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(dataSyncBoundaryTime));
        List<Map<String, Object>> answerConceptMigrations = runInOrgContext(() -> jdbcTemplate.queryForList(query), jdbcTemplate);
        answerConceptMigrations.forEach(acm -> performMigration(acm, tableMetadata));
    }

    private void performMigration(Map<String, Object> acm, TableMetadata tableMetadata) {
        String query = format("update \"%s\".\"%s\" set \"%s\" = '%s' where \"%s\" = '%s';",
                ContextHolder.getDbSchema(), tableMetadata.getName(), acm.get("name"),
                acm.get("new_answer_concept_name"), acm.get("name"), acm.get("old_answer_concept_name"));
        runInOrgContext(() -> {
            jdbcTemplate.execute(query);
            return NullObject.instance();
        }, jdbcTemplate);
    }
}
