package org.avniproject.etl.service;

import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.Organisation;
import org.avniproject.etl.domain.metadata.SchemaMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.avniproject.etl.domain.syncstatus.EntitySyncStatus;
import org.avniproject.etl.domain.syncstatus.SchemaDataSyncStatus;
import org.avniproject.etl.repository.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SyncService {

    private EntityRepository entityRepository;

    @Autowired
    public SyncService(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    public void sync(Organisation organisation) {
        SchemaMetadata currentSchemaMetadata = organisation.getSchemaMetadata();
        currentSchemaMetadata.getTableMetadata().forEach(tableMetadata -> migrateTable(tableMetadata, organisation.getSyncStatus()));
    }

    private void migrateTable(TableMetadata tableMetadata, SchemaDataSyncStatus syncStatus) {
        if (tableMetadata.getType().equals(TableMetadata.Type.Individual)) {
                EntitySyncStatus entitySyncStatus = syncStatus.getEntitySyncStatus(tableMetadata);
                entitySyncStatus.setSyncStatus(EntitySyncStatus.Status.Running);
                entityRepository.save(entitySyncStatus);

                entityRepository.saveEntities(tableMetadata, entitySyncStatus.getLastSyncTime(), ContextHolder.dataSyncBoundaryTime());


                entitySyncStatus.setSyncStatus(EntitySyncStatus.Status.Success);
                entitySyncStatus.setSyncStatus(EntitySyncStatus.Status.Success);
                entityRepository.save(entitySyncStatus);
        }

    }
}
