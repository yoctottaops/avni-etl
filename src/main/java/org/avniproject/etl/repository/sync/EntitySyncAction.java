package org.avniproject.etl.repository.sync;

import org.avniproject.etl.domain.metadata.SchemaMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;

import java.util.Date;

public interface EntitySyncAction {
    boolean doesntSupport(TableMetadata tableMetadata);
    void perform(TableMetadata tableMetadata, Date lastSyncTime, Date dataSyncBoundaryTime, SchemaMetadata currentSchemaMetadata);
}
