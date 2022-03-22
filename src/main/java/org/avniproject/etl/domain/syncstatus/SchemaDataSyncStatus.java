package org.avniproject.etl.domain.syncstatus;

import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.Model;
import org.avniproject.etl.domain.metadata.TableMetadata;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
                .orElse(new EntitySyncStatus(tableMetadata.getId(), new Date("1970-01-01"), ContextHolder.getStartTime(), EntitySyncStatus.Status.Running));
    }

    public static SchemaDataSyncStatus createNewFrom(SchemaDataSyncStatus oldSyncStatus) {
        Date tillDate = nowMinus10Seconds();
        List<EntitySyncStatus> newEntitySyncStatusList = oldSyncStatus.entitySyncStatusList.stream().map(entitySyncStatus -> entitySyncStatus.nextStatus(entitySyncStatus, tillDate)).collect(Collectors.toList());
        return new SchemaDataSyncStatus(newEntitySyncStatusList);
    }

    private static Date nowMinus10Seconds() {
        return toDate(LocalDateTime.now().minus(Duration.ofSeconds(10)));
    }

    private static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
