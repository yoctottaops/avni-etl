package org.avniproject.etl.repository.sync;

import org.avniproject.etl.domain.metadata.TableMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class AddressTableSyncAction implements EntitySyncAction {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public AddressTableSyncAction(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public boolean supports(TableMetadata tableMetadata) {
        return tableMetadata.getType().equals(TableMetadata.Type.Address);
    }

    @Override
    public void perform(TableMetadata tableMetadata, Date lastSyncTime, Date dataSyncBoundaryTime) {
        if (!this.supports(tableMetadata)) {
            return;
        }
        //todo: Perform actual sync
    }
}
