package org.avniproject.etl.repository.sync;

import org.avniproject.etl.domain.OrgIdentityContextHolder;
import org.avniproject.etl.domain.NullObject;
import org.avniproject.etl.domain.metadata.SchemaMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.avniproject.etl.repository.JdbcContextWrapper.runInOrgContext;

@Repository
public class EntityApprovalStatusSyncAction implements EntitySyncAction {

    private final JdbcTemplate jdbcTemplate;
    private final Map<TableMetadata.Type, String> typeMap = new HashMap<>();

    @Autowired
    public EntityApprovalStatusSyncAction(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        typeMap.put(TableMetadata.Type.Household, "Subject");
        typeMap.put(TableMetadata.Type.Individual, "Subject");
        typeMap.put(TableMetadata.Type.Person, "Subject");
        typeMap.put(TableMetadata.Type.Group, "Subject");
        typeMap.put(TableMetadata.Type.Encounter, "Encounter");
        typeMap.put(TableMetadata.Type.ProgramEnrolment, "ProgramEnrolment");
        typeMap.put(TableMetadata.Type.ProgramExit, "ProgramEnrolment");
        typeMap.put(TableMetadata.Type.ProgramEncounter, "ProgramEncounter");
        typeMap.put(TableMetadata.Type.ProgramEncounterCancellation, "ProgramEncounter");
        typeMap.put(TableMetadata.Type.IndividualEncounterCancellation, "Encounter");
    }

    @Override
    public boolean doesntSupport(TableMetadata tableMetadata) {
        return !typeMap.containsKey(tableMetadata.getType());
    }

    @Override
    public void perform(TableMetadata tableMetadata, Date lastSyncTime, Date dataSyncBoundaryTime, SchemaMetadata currentSchemaMetadata) {
        if (this.doesntSupport(tableMetadata)) {
            return;
        }
        updateEntitySyncStatus(tableMetadata);
    }

    private void updateEntitySyncStatus(TableMetadata tableMetadata) {
        String baseSql = "update \"${schemaName}\".\"${tableName}\" t1\n" +
                "set latest_approval_status = status\n" +
                "from (select eas.entity_id                                                               as entity_id,\n" +
                "             approval_status.status                                                      as status,\n" +
                "             row_number()\n" +
                "             over (partition by eas.entity_id order by eas.status_date_time desc) as row_number\n" +
                "      from entity_approval_status eas\n" +
                "               join approval_status on eas.approval_status_id = approval_status.id\n" +
                "      where eas.is_voided = false\n" +
                "        and eas.entity_type = '${entityType}'\n" +
                "     ) eas\n" +
                "where eas.entity_id = t1.id\n" +
                "  and row_number = 1\n" +
                "  and status notnull;";
        String sql = baseSql
                .replace("${schemaName}", OrgIdentityContextHolder.getDbSchema())
                .replace("${tableName}", tableMetadata.getName())
                .replace("${entityType}", typeMap.get(tableMetadata.getType()));

        runInOrgContext(() -> {
            jdbcTemplate.execute(sql);
            return NullObject.instance();
        }, jdbcTemplate);
    }
}
