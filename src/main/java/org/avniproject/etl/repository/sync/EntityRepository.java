package org.avniproject.etl.repository.sync;

import org.avniproject.etl.domain.metadata.TableMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class EntityRepository {
    private List<EntitySyncAction> entitySyncRepositories = new ArrayList<>();

    @Autowired
    public EntityRepository(TransactionalTablesSyncAction transactionalTablesSyncRepository) {
        entitySyncRepositories.add(transactionalTablesSyncRepository);
    }

    public void saveEntities(TableMetadata tableMetadata, Date lastSyncTime, Date dataSyncBoundaryTime) {
        entitySyncRepositories.forEach(entitySyncRepository -> {
            entitySyncRepository.perform(tableMetadata, lastSyncTime, dataSyncBoundaryTime);
        });
    }
}
