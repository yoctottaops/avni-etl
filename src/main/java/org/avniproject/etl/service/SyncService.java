package org.avniproject.etl.service;

import org.avniproject.etl.domain.Organisation;
import org.avniproject.etl.domain.metadata.SchemaMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.avniproject.etl.domain.syncstatus.SchemaDataSyncStatus;
import org.avniproject.etl.repository.EntityRepository;
import org.springframework.stereotype.Service;

@Service
public class SyncService {

    private EntityRepository entityRepository;

    public void sync(Organisation organisation) {
        SchemaMetadata currentSchemaMetadata = organisation.getSchemaMetadata();
        currentSchemaMetadata.getTableMetadata().forEach(tableMetadata -> migrateTable(tableMetadata, organisation.getSyncStatus()));
    }

    private void migrateTable(TableMetadata tableMetadata, SchemaDataSyncStatus syncStatus) {
    }
}
