package org.avniproject.etl.repository;

import org.avniproject.etl.domain.OrganisationIdentity;
import org.avniproject.etl.repository.rowMappers.OrganisationIdentityRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.avniproject.etl.repository.JdbcContextWrapper.runInOrgContext;

@Repository
public class OrganisationRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrganisationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<OrganisationIdentity> getOrganisationList() {
        String query = "select db_user, schema_name AS schema_name\n" +
                "from organisation\n" +
                "union all\n" +
                "select o.db_user, og.schema_name AS schema_name\n" +
                "from organisation_group og\n" +
                "         inner join organisation_group_organisation ogo on og.id = ogo.organisation_group_id\n" +
                "         inner join organisation o on ogo.organisation_id = o.id\n";

        List<OrganisationIdentity> organisationIdentities = jdbcTemplate.query(query, new OrganisationIdentityRowMapper());
        String schemaToDbUserQuery = "select og.schema_name, string_agg(o.db_user, ',') || ',' || og.db_user db_users\n" +
                "from organisation_group og\n" +
                "         join organisation_group_organisation ogo on og.id = ogo.organisation_group_id\n" +
                "         join organisation o on ogo.organisation_id = o.id\n" +
                "group by og.schema_name, og.db_user";
        List<Map<String, Object>> schemaToDbUsers = jdbcTemplate.queryForList(schemaToDbUserQuery);
        organisationIdentities.forEach(organisationIdentity -> {
            populateGroupDbUsers(organisationIdentity, schemaToDbUsers);
        });
        return organisationIdentities;
    }

    public OrganisationIdentity getOrganisation(String organisationUUID) {
        String query = "select db_user, schema_name\n" +
                "from organisation where uuid = ? and is_voided = false";
        List<OrganisationIdentity> organisationIdentities = jdbcTemplate.query(query, ps -> ps.setString(1, organisationUUID), new OrganisationIdentityRowMapper());
        if (organisationIdentities.size() == 0) return null;

        OrganisationIdentity organisationIdentity = organisationIdentities.get(0);
        String schemaToDbUserQuery = "select og.schema_name, string_agg(o.db_user, ',') || ',' || og.db_user db_users\n" +
                "from organisation_group og\n" +
                "         join organisation_group_organisation ogo on og.id = ogo.organisation_group_id\n" +
                "         join organisation o on ogo.organisation_id = o.id\n" +
                "         where o.uuid = ?\n" +
                "group by og.schema_name, og.db_user";
        List<Map<String, Object>> schemaToDbUsers = jdbcTemplate.query(schemaToDbUserQuery, ps -> ps.setString(1, organisationUUID), (rs, rowNum) -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put(rs.getString(1), rs.getString(2));
            return map;
        });
        this.populateGroupDbUsers(organisationIdentity, schemaToDbUsers);
        return organisationIdentity;
    }

    private void populateGroupDbUsers(OrganisationIdentity organisationIdentity, List<Map<String, Object>> schemaToDbUsers) {
        String schemaName = organisationIdentity.getSchemaName();
        Map<String, Object> dbUsers = schemaToDbUsers.stream().filter(s -> Objects.equals(s.get("schema_name"), schemaName)).findFirst().orElse(null);
        if (dbUsers != null) {
            String dbUserString = (String) dbUsers.get("db_users");
            organisationIdentity.setGroupDbUsers(Arrays.asList(dbUserString.split(",")));
        }
    }

    public void createDBUser(String name, String pass) {
        jdbcTemplate.queryForMap("select create_db_user(?, ?)", name, pass);
    }

    public void createImplementationSchema(String schemaName, String dbUser) {
        jdbcTemplate.queryForMap("select create_implementation_schema(?, ?)", schemaName, dbUser);
    }

    //Written for testing of runInOrgContext. Will remove when real queries come in
    public Integer getCountOfOrganisationsWithSetRole() {
        return runInOrgContext(() -> jdbcTemplate.queryForObject("select count(*) from organisation", Integer.class), jdbcTemplate);
    }
}
