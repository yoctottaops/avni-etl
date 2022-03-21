package org.avniproject.etl.domain.syncstatus;

import org.avniproject.etl.domain.Model;

import java.util.Date;

public class EntitySyncStatus extends Model {
    public enum EntityType {
        Address,
        Catchment,
        Concept,
        Users,
        Gender,
        Subject,
        Checklist,
        Encounter,
        FormMapping,
        FormElement,
        ProgramEnrolment,
        ProgramEncounter,
    }

    public enum Status {
        Running,
        Success,
        Failure
    }

    private EntityType entityName;
    private Date syncStartTime;
    private Date syncEndTime;
    private Status syncStatus;

    public EntitySyncStatus(EntityType entityName, Date syncStartTime, Date syncEndTime, Status syncStatus) {
        this.entityName = entityName;
        this.syncStartTime = syncStartTime;
        this.syncEndTime = syncEndTime;
        this.syncStatus = syncStatus;
    }

    public EntitySyncStatus nextStatus(EntitySyncStatus entitySyncStatus, Date tillDate) {
        return new EntitySyncStatus(entitySyncStatus.entityName, entitySyncStatus.syncEndTime, tillDate, Status.Running);
    }

    public void markSuccess() {
        this.syncStatus = Status.Success;
    }

    public void markFailure() {
        this.syncStatus = Status.Failure;
    }
}