package org.avniproject.etl.controller;

import org.avniproject.etl.service.MediaService;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;


import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class MediaControllerTest {

    @Test
    @Sql({"/test-data-teardown.sql", "/test-data-for-api.sql"})
    @Sql(scripts = {"/test-data-teardown.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldGetAllIfEntityFromMediaTable() {
        MediaService mediaService = mock(MediaService.class);
        when(mediaService.list("orga", 5, 0)).thenReturn(new ArrayList<>());
        MediaController mediaController = new MediaController(mediaService);
        mediaController.getMedia("orga", "orga", 5, 0);
        verify(mediaService);
    }

}
