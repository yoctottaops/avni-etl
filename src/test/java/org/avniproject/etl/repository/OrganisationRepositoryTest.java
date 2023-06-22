package org.avniproject.etl.repository;

import org.avniproject.etl.BaseIntegrationTest;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class OrganisationRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private OrganisationRepository organisationRepository;

    @Test
    @Sql({"/test-data-teardown.sql", "/organisation-group.sql"})
    @Sql(scripts = "/test-data-teardown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldRetrieveAllOrganisationsSeparatelyForOrganisationGroup() {
        List<OrganisationIdentity> organisationList = organisationRepository.getOrganisationGroup("og");
        List<OrganisationIdentity> groupOrganisation = organisationList.stream().filter(organisationIdentity -> organisationIdentity.getSchemaName().equals("og")).collect(Collectors.toList());
        assertThat(groupOrganisation.size(), equalTo(2));
        assertThat(groupOrganisation, hasItem(allOf(hasProperty("dbUser", equalTo("ogi1")),hasProperty("schemaName", equalTo("og")))));
        assertThat(groupOrganisation, hasItem(allOf(hasProperty("dbUser", equalTo("ogi2")),hasProperty("schemaName", equalTo("og")))));
    }

    @Test
    public void shouldRunStoredProcedureToCreateDbUser() {
        organisationRepository.createDBUser("newuser", "password");
    }

    @Test
    public void shouldNotFailWhenCreatingSchemaMultipleTimes() {
        organisationRepository.createDBUser("newuser", "password");
        organisationRepository.createImplementationSchema("newuser", "newuser");

        organisationRepository.createDBUser("newuser", "password");
        organisationRepository.createImplementationSchema("newuser", "newuser");
    }
}
