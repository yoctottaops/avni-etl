package org.avniproject.etl.repository.sync;

import org.avniproject.etl.domain.OrgIdentityContextHolder;
import org.avniproject.etl.domain.NullObject;
import org.avniproject.etl.domain.metadata.ColumnMetadata;
import org.avniproject.etl.domain.metadata.SchemaMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.avniproject.etl.repository.sql.TransactionalSyncSqlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.avniproject.etl.repository.JdbcContextWrapper.runInOrgContext;
import static org.avniproject.etl.repository.JdbcContextWrapper.runInSchemaUserContext;

@Repository
public class AnswerConceptSync implements EntitySyncAction {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AnswerConceptSync(JdbcTemplate jdbcTemplate) {
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
        String query = format("select acm.old_answer_concept_name, acm.new_answer_concept_name, cm.name, cm.concept_type\n" +
                        "from answer_concept_migration acm\n" +
                        "         join column_metadata cm on acm.concept_id = cm.concept_id\n" +
                        "where acm.is_voided = false\n" +
                        "  and cm.table_id = %d\n" +
                        "  and acm.last_modified_date_time > '%s'\n" +
                        "  and acm.last_modified_date_time <= '%s'\n" +
                        "order by acm.last_modified_date_time asc;",
                tableMetadata.getId(),
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(lastSyncTime),
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(dataSyncBoundaryTime));
        List<Map<String, Object>> answerConceptMigrations = runInSchemaUserContext(() -> jdbcTemplate.queryForList(query), jdbcTemplate);
        answerConceptMigrations.forEach(acm -> performMigration(acm, tableMetadata));
    }

    private void performMigration(Map<String, Object> acm, TableMetadata tableMetadata) {
        String columnName = (String) acm.get("name");
        String newAnswerConceptName = (String) acm.get("new_answer_concept_name");
        String oldAnswerConceptName = (String) acm.get("old_answer_concept_name");
        String updateTemplate = format("update \"%s\".\"%s\" set \"%s\" = ${updateCondition} where \"%s\" ${whereCondition};",
                OrgIdentityContextHolder.getDbSchema(), tableMetadata.getName(), columnName, columnName);
        String query = ColumnMetadata.ConceptType.MultiSelect.name().equals(acm.get("concept_type")) ?
                getMultiSelectUpdateQuery(updateTemplate, oldAnswerConceptName, newAnswerConceptName, columnName) :
                getSingleSelectUpdateQuery(updateTemplate, oldAnswerConceptName, newAnswerConceptName);
        runInOrgContext(() -> {
            jdbcTemplate.execute(query);
            return NullObject.instance();
        }, jdbcTemplate);
    }

    private String getMultiSelectUpdateQuery(String baseQuery, String oldName, String newName, String columnName) {
        String startPositionUpdate = baseQuery.replace("${updateCondition}", format("regexp_replace(\"%s\", '^%s,', '%s,')", columnName, oldName, newName))
                .replace("${whereCondition}", format(" ~ '^%s,'", oldName));
        String endPositionUpdate = baseQuery.replace("${updateCondition}", format("regexp_replace(\"%s\", ', %s$', ', %s')", columnName, oldName, newName))
                .replace("${whereCondition}", format(" ~ ', %s$'", oldName));
        String middlePositionUpdate = baseQuery.replace("${updateCondition}", format("regexp_replace(\"%s\", ', %s,', ', %s,')", columnName, oldName, newName))
                .replace("${whereCondition}", format(" ~ ', %s,'", oldName));
        return String.join("\n", startPositionUpdate, endPositionUpdate, middlePositionUpdate);
    }

    private String getSingleSelectUpdateQuery(String baseQuery, String oldName, String newName) {
        return baseQuery.replace("${updateCondition}", format("'%s'", newName))
                .replace("${whereCondition}", format(" = '%s'", oldName));
    }
}
