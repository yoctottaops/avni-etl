package org.avniproject.etl.config;

import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.avniproject.etl.repository.JdbcContextWrapper.runInOrgContext;

@Component
public class ContextHolderUtil {


    private final JdbcTemplate jdbcTemplate;

    Map<String, Object> parameters = new HashMap<>(1);

    @Autowired
    public ContextHolderUtil(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setParameters(String orgUUID) {
        String sql = "SELECT * FROM organisation WHERE uuid = '"+ orgUUID +"'";
        OrganisationIdentity org =  jdbcTemplate.queryForObject(sql, (rs, rowNum) -> setOrganisation(rs));

        ContextHolder.setContext(org);

        parameters.put("schema", ContextHolder.getDbSchema());
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public String getSchemaName() {
        return String.valueOf(parameters.get("schema"));
    }

    private OrganisationIdentity setOrganisation(ResultSet rs)  throws SQLException{
        return new OrganisationIdentity(rs.getString("schema_name"), rs.getString("db_user"));
    }
}
