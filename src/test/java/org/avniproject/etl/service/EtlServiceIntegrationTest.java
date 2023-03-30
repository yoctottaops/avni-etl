package org.avniproject.etl.service;

import org.avniproject.etl.BaseIntegrationTest;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class EtlServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private EtlService etlService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Sql({"/test-data-teardown.sql", "/test-data.sql"})
    @Sql(scripts = {"/test-data-teardown.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldRunForOrganisation() {
        etlService.run();
    }

    @Test
    @Sql({"/test-data-teardown.sql", "/test-data.sql"})
    @Sql(scripts = {"/test-data-teardown.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldNotFailWhenRunTwice() {
        etlService.runForOrganisation(new OrganisationIdentity("orgc", "orgc"));
        etlService.runForOrganisation(new OrganisationIdentity("orgc", "orgc"));

        assertThat(countOfRowsIn("orgc.goat"), equalTo(1L));
        assertThat(countOfRowsIn("orgc.household"), equalTo(1L));
        assertThat(countOfRowsIn("orgc.person"), equalTo(1L));
        assertThat(countOfRowsIn("orgc.person_nutrition"), equalTo(2L));
        assertThat(countOfRowsIn("orgc.person_nutrition_exit"), equalTo(1L));
        assertThat(countOfRowsIn("orgc.person_nutrition_growth_monitoring"), equalTo(1L));
        assertThat(countOfRowsIn("orgc.person_nutrition_growth_monitoring_cancel"), equalTo(1L));
    }

    @Test
    @Sql({"/test-data-teardown.sql", "/organisation-group.sql"})
//    @Sql(scripts = {"/test-data-teardown.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldRunForOrganisationGroup() {
        etlService.run();
        assertThat(countOfRowsIn("og.goat"), equalTo(2L));
        assertThat(countOfRowsIn("og.household"), equalTo(2L));
        assertThat(countOfRowsIn("og.person"), equalTo(2L));
    }

    private Long countOfRowsIn(String tableName) {
        List<Map<String, Object>> countResult = jdbcTemplate.queryForList(String.format("select count(*) as cnt from %s", tableName));
        return (Long) countResult.get(0).get("cnt");
    }
}
