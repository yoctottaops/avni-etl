package org.avniproject.etl.service;

import org.avniproject.etl.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class NoIAMAuthServiceTest extends BaseIntegrationTest {
    @Autowired
    private NoIAMAuthService noIAMAuthService;

    @Test
    public void getUserForTokenShouldFailIfImproperlyConfigured() {
        noIAMAuthService.getUserFromToken("someUserName");
    }
}
