package org.avniproject.etl.repository.rowMappers;

import org.avniproject.etl.domain.OrganisationIdentity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrganisationIdentityRowMapper implements RowMapper<OrganisationIdentity> {
    @Override
    public OrganisationIdentity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new OrganisationIdentity(rs.getInt("id"),
                rs.getString("db_user"),
                rs.getString("schema_name"),
                OrganisationIdentity.OrganisationType.valueOf(rs.getString("organisation_type"))
        );
    }
}
