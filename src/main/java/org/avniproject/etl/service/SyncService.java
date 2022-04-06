package org.avniproject.etl.service;

import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.Organisation;
import org.avniproject.etl.domain.metadata.SchemaMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.avniproject.etl.domain.syncstatus.EntitySyncStatus;
import org.avniproject.etl.domain.syncstatus.SchemaDataSyncStatus;
import org.avniproject.etl.repository.EntityRepository;
import org.avniproject.etl.repository.EntitySyncStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SyncService {

    private EntityRepository entityRepository;
    private EntitySyncStatusRepository entitySyncStatusRepository;
    private static final Logger log = LoggerFactory.getLogger(SyncService.class);

    @Autowired
    public SyncService(EntityRepository entityRepository, EntitySyncStatusRepository entitySyncStatusRepository) {
        this.entityRepository = entityRepository;
        this.entitySyncStatusRepository = entitySyncStatusRepository;
    }

    public void sync(Organisation organisation) {
        SchemaMetadata currentSchemaMetadata = organisation.getSchemaMetadata();
        currentSchemaMetadata.getTableMetadata().forEach(tableMetadata -> migrateTable(tableMetadata, organisation.getSyncStatus()));
    }

    private void migrateTable(TableMetadata tableMetadata, SchemaDataSyncStatus syncStatus) {
        log.info(String.format("Migrating table %s.%s", ContextHolder.getDbSchema(), tableMetadata.getName()));
        if (tableMetadata.getType().equals(TableMetadata.Type.Individual)
                || tableMetadata.getType().equals(TableMetadata.Type.Person)
                || tableMetadata.getType().equals(TableMetadata.Type.Encounter)
                || tableMetadata.getType().equals(TableMetadata.Type.ProgramEnrolment)) {
            EntitySyncStatus entitySyncStatus = syncStatus.getEntitySyncStatus(tableMetadata);
            entitySyncStatus.setSyncStatus(EntitySyncStatus.Status.Running);
            entitySyncStatusRepository.save(entitySyncStatus);

            entityRepository.saveEntities(tableMetadata, entitySyncStatus.getLastSyncTime(), ContextHolder.dataSyncBoundaryTime());

            entitySyncStatus.markSuccess(ContextHolder.dataSyncBoundaryTime());
            entitySyncStatusRepository.save(entitySyncStatus);
        }

    }
}
