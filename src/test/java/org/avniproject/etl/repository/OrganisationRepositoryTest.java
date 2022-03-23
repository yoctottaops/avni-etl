package org.avniproject.etl.repository;

import org.avniproject.etl.BaseIntegrationTest;
import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Sql("/test-data.sql")
public class OrganisationRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private OrganisationRepository organisationRepository;

    @Test
    public void shouldRetrieveAllOrganisationsWithAnalyticsDbTurnedOn() {
        List<OrganisationIdentity> organisationList = organisationRepository.getOrganisationList();
        assertThat(organisationList, hasItem(hasProperty("dbUser", equalTo("orgb") )));
        assertThat(organisationList, hasItem(hasProperty("schemaName", equalTo("orgb") )));
        assertThat(organisationList, not(hasItem(hasProperty("dbUser", equalTo("orga") ))));
    }

    @Test
    public void shouldRetrieveAllOrganisationGroupsWithAnalyticsDbTurnedOn() {
        List<OrganisationIdentity> organisationList = organisationRepository.getOrganisationList();
        assertThat(organisationList, not(hasItem(hasProperty("dbUser", equalTo("org_group_b") ))));
        assertThat(organisationList, hasItem(hasProperty("dbUser", equalTo("org_group_a") )));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void test() {
        ContextHolder.create(new OrganisationIdentity(12, "orgb", "orgb", OrganisationIdentity.OrganisationType.Organisation));
        List<OrganisationIdentity> organisationList = organisationRepository.getOrganisationList();
        ContextHolder.create(organisationList.stream().filter(organisationIdentity -> organisationIdentity.getDbUser().equals("orgb")).findFirst().get());
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
