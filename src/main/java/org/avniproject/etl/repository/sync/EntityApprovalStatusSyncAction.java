package org.avniproject.etl.repository.sync;

import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.NullObject;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.avniproject.etl.repository.dynamicInsert.TransactionalSyncSqlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.avniproject.etl.repository.JdbcContextWrapper.runInOrgContext;

@Repository
public class EntityApprovalStatusSyncAction implements EntitySyncAction {

    private JdbcTemplate jdbcTemplate;
    private Map<TableMetadata.Type, String> typeMap = new HashMap<>();

    @Autowired
    public EntityApprovalStatusSyncAction(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        typeMap.put(TableMetadata.Type.Household, "Subject");
        typeMap.put(TableMetadata.Type.Individual, "Subject");
        typeMap.put(TableMetadata.Type.Person, "Subject");
        typeMap.put(TableMetadata.Type.Encounter, "Encounter");
        typeMap.put(TableMetadata.Type.ProgramEnrolment, "ProgramEnrolment");
        typeMap.put(TableMetadata.Type.ProgramExit, "ProgramEnrolment");
        typeMap.put(TableMetadata.Type.ProgramEncounter, "ProgramEncounter");
        typeMap.put(TableMetadata.Type.ProgramEncounterCancellation, "ProgramEncounter");
        typeMap.put(TableMetadata.Type.IndividualEncounterCancellation, "Encounter");
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
        updateEntitySyncStatus(tableMetadata);
    }

    private void updateEntitySyncStatus(TableMetadata tableMetadata) {
        String baseSql = "update \"${schemaName}\".\"${tableName}\" t1\n" +
                "set latest_approval_status = (select \"as\".status\n" +
                "                                 from entity_approval_status eas\n" +
                "                                   join approval_status \"as\" on eas.approval_status_id = \"as\".id\n" +
                "                                 where eas.entity_id = t1.id\n" +
                "                                   and eas.is_voided is false\n" +
                "                                   and eas.entity_type = '${entityType}'\n" +
                "                                 order by eas.last_modified_date_time desc\n" +
                "                                 limit 1);";
        String sql = baseSql
                .replace("${schemaName}", ContextHolder.getDbSchema())
                .replace("${tableName}", tableMetadata.getName())
                .replace("${entityType}", typeMap.get(tableMetadata.getType()));

        runInOrgContext(() -> {
            jdbcTemplate.execute(sql);
            return NullObject.instance();
        }, jdbcTemplate);
    }
}
