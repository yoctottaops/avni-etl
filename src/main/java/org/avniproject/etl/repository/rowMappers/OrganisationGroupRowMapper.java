package org.avniproject.etl.repository.rowMappers;

import org.avniproject.etl.domain.OrganisationIdentity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrganisationGroupRowMapper implements RowMapper<OrganisationIdentity> {
    @Override
    public OrganisationIdentity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return OrganisationIdentity.createForOrganisationGroup(rs.getString("db_user"),
                rs.getString("schema_name"),
                rs.getString("schema_db_user")
        );
    }
}
