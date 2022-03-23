package org.avniproject.etl.repository;

import org.avniproject.etl.domain.metadata.diff.Diff;
import org.avniproject.etl.repository.rowMappers.OrganisationIdentityRowMapper;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.avniproject.etl.repository.JdbcContextWrapper.runInOrgContext;

@Repository
public class OrganisationRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrganisationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<OrganisationIdentity> getOrganisationList() {
        String query = "select id, db_user, 'Organisation' organisation_type, schema_name schema_name\n" +
                "from organisation\n" +
                "where has_analytics_db = true\n" +
                "union all\n" +
                "select id, db_user, 'OrganisationGroup' organisation_type, schema_name schema_name\n" +
                "from organisation_group\n" +
                "where has_analytics_db = true;";

        List<OrganisationIdentity> organisationIdentityMapper = jdbcTemplate.query(query, new OrganisationIdentityRowMapper());
        return organisationIdentityMapper;
    }

    public void createDBUser(String name, String pass) {
        jdbcTemplate.queryForMap("select create_db_user(?, ?)", name, pass);
    }

    public void createImplementationSchema(String schemaName, String dbUser) {
        jdbcTemplate.queryForMap("select create_implementation_schema(?, ?)", schemaName, dbUser);
    }

    //Written for testing of runInOrgContext. Will remove when real queries come in
    public Integer getCountOfOrganisationsWithSetRole() {
        return  runInOrgContext(() -> jdbcTemplate.queryForObject("select count(*) from organisation", Integer.class), jdbcTemplate);
    }

    public void applyChanges(List<Diff> diffs) {

    }
}
