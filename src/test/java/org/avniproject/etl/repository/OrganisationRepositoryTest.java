package org.avniproject.etl.repository;

import org.avniproject.etl.BaseIntegrationTest;
import org.avniproject.etl.domain.ContextHolder;
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
    @Sql({"/test-data.sql"})
    @Sql(scripts = {"/test-data-teardown.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldRetrieveAllOrganisationsWithAnalyticsDbTurnedOn() {
        List<OrganisationIdentity> organisationList = organisationRepository.getOrganisationList();
        assertThat(organisationList, hasItem(hasProperty("dbUser", equalTo("orgb") )));
        assertThat(organisationList, hasItem(hasProperty("schemaName", equalTo("orgb") )));
        assertThat(organisationList, not(hasItem(hasProperty("dbUser", equalTo("orga") ))));
    }

    @Test
    @Sql({"/test-data-teardown.sql", "/organisation-group.sql"})
    @Sql(scripts = "/test-data-teardown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldRetrieveAllOrganisationsSeparatelyForOrganisationGroup() {
        List<OrganisationIdentity> organisationList = organisationRepository.getOrganisationList();
        List<OrganisationIdentity> groupOrganisation = organisationList.stream().filter(organisationIdentity -> organisationIdentity.getSchemaName().equals("og")).collect(Collectors.toList());
        assertThat(groupOrganisation.size(), equalTo(2));
        assertThat(groupOrganisation, hasItem(allOf(hasProperty("dbUser", equalTo("ogi1")),hasProperty("schemaName", equalTo("og")))));
        assertThat(groupOrganisation, hasItem(allOf(hasProperty("dbUser", equalTo("ogi2")),hasProperty("schemaName", equalTo("og")))));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void test() {
        ContextHolder.setContext(new OrganisationIdentity("orgb", "orgb"));
        List<OrganisationIdentity> organisationList = organisationRepository.getOrganisationList();
        ContextHolder.setContext(organisationList.stream().filter(organisationIdentity -> organisationIdentity.getDbUser().equals("orgb")).findFirst().get());
        assertThat(organisationRepository.getCountOfOrganisationsWithSetRole(), is(1));
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
