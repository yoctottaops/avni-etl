package org.avniproject.etl.service;

import org.avniproject.etl.BaseIntegrationTest;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@Sql("/test-data.sql")
public class EtlServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private EtlService etlService;

    @Test
    public void shouldNotFailWhenRunTwice() {
        etlService.runForOrganisation(new OrganisationIdentity("orgc", "orgc"));
        etlService.runForOrganisation(new OrganisationIdentity("orgc", "orgc"));
    }
}
