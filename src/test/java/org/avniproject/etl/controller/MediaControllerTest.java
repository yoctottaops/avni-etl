package org.avniproject.etl.controller;

import org.avniproject.etl.config.ContextHolderUtil;
import org.avniproject.etl.dto.ResponseDTO;
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
        ContextHolderUtil contextHolderUtil = mock(ContextHolderUtil.class);
        when(mediaService.list(5, 0)).thenReturn(new ResponseDTO(5,0, new ArrayList<>()));
        MediaController mediaController = new MediaController(mediaService, contextHolderUtil);
        mediaController.getMedia("4640a909-dfae-4ec3-8fbb-1b08f35c4995", 5, 0);
        verify(mediaService).list(5, 0);
    }

}
