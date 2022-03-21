package org.avniproject.etl.domain.syncstatus;

import org.avniproject.etl.domain.Model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SchemaDataSyncStatus extends Model {
    private List<EntitySyncStatus> entitySyncStatusList;

    public SchemaDataSyncStatus(List<EntitySyncStatus> entitySyncStatusList) {
        this.entitySyncStatusList = entitySyncStatusList;
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
