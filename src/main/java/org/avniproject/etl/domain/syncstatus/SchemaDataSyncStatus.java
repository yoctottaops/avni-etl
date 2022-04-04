package org.avniproject.etl.domain.syncstatus;

import org.avniproject.etl.domain.Model;
import org.avniproject.etl.domain.metadata.TableMetadata;

import java.util.Date;
import java.util.List;

public class SchemaDataSyncStatus extends Model {
    private final List<EntitySyncStatus> entitySyncStatusList;

    public SchemaDataSyncStatus(List<EntitySyncStatus> entitySyncStatusList) {
        this.entitySyncStatusList = entitySyncStatusList;
    }

    public EntitySyncStatus getEntitySyncStatus(TableMetadata tableMetadata) {
        return entitySyncStatusList
                .stream()
                .filter(entitySyncStatus -> entitySyncStatus.getTableMetadataId().equals(tableMetadata.getId()))
                .findFirst()
                .orElse(new EntitySyncStatus(tableMetadata.getId(), new Date(0), EntitySyncStatus.Status.Running));
    }
}
