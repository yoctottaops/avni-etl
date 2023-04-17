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

import static java.lang.String.format;


@Component
public class ContextHolderUtil {

    private JdbcTemplate jdbcTemplate;

    static Map<String, Object> parameters = new HashMap<>();

    @Autowired
    public ContextHolderUtil(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setParameters(Long orgID) {
        String sql = format("SELECT * FROM organisation WHERE id = %s", orgID);
        OrganisationIdentity org =  jdbcTemplate.queryForObject(sql, (rs, rowNum) -> setOrganisation(rs));

        ContextHolder.setContext(org);

        parameters.put("schema", ContextHolder.getDbSchema());
    }

    public static String getSchemaName() {
        return String.valueOf(parameters.get("schema"));
    }

    private static OrganisationIdentity setOrganisation(ResultSet rs)  throws SQLException{
        return new OrganisationIdentity(rs.getString("schema_name"), rs.getString("db_user"));
    }
}
