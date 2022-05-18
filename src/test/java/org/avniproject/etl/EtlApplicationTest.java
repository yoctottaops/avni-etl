package org.avniproject.etl;

import org.avniproject.etl.service.EtlService;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class EtlApplicationTest {
    @Test
    public void shouldCallEtlServiceToRunTheJob() throws Exception {
        EtlService etlService = mock(EtlService.class);
        EtlApplication etlApplication = new EtlApplication(etlService);
        etlApplication.run();
        verify(etlService).runForOrganisationSchemaNames(anyList());
    }
}
