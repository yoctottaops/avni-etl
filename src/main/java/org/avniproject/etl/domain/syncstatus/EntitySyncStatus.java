package org.avniproject.etl.domain.syncstatus;

import org.avniproject.etl.domain.Model;

import java.util.Date;

public class EntitySyncStatus extends Model {
    public enum Status {
        Running,
        Success,
        Failure
    }

    private final Integer tableMetadataId;
    private final Date syncStartTime;
    private final Date syncEndTime;
    private Status syncStatus;

    public EntitySyncStatus(Integer id, Integer tableMetadataId, Date syncStartTime, Date syncEndTime, Status syncStatus) {
        super(id);
        this.tableMetadataId = tableMetadataId;
        this.syncStartTime = syncStartTime;
        this.syncEndTime = syncEndTime;
        this.syncStatus = syncStatus;
    }

    public EntitySyncStatus(Integer tableMetadataId, Date syncStartTime, Date syncEndTime, Status syncStatus) {
        this(null, tableMetadataId, syncStartTime, syncEndTime, syncStatus);
    }

    public EntitySyncStatus nextStatus(EntitySyncStatus entitySyncStatus, Date tillDate) {
        return new EntitySyncStatus(getId(), tableMetadataId, entitySyncStatus.syncEndTime, tillDate, Status.Running);
    }

    public void markSuccess() {
        this.syncStatus = Status.Success;
    }

    public void markFailure() {
        this.syncStatus = Status.Failure;
    }

    public Integer getTableMetadataId() {
        return tableMetadataId;
    }
}