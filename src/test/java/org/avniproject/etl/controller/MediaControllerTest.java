package org.avniproject.etl.controller;

import org.avniproject.etl.BaseIntegrationTest;
import org.avniproject.etl.config.AmazonClientService;
import org.avniproject.etl.config.ContextHolderUtil;
import org.avniproject.etl.dto.ResponseDTO;
import org.avniproject.etl.repository.OrganisationRepository;
import org.avniproject.etl.repository.service.MediaTableRepositoryService;
import org.avniproject.etl.service.MediaService;
import org.avniproject.etl.util.GenericUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;


import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MediaControllerTest extends BaseIntegrationTest {

    @Autowired
    private GenericUtil genericUtil;

    @Test
    @Sql({"/test-data-teardown.sql", "/test-data-for-api.sql"})
    @Sql(scripts = {"/test-data-teardown.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldGetAllIfEntityFromMediaTable() {
        MediaService mediaService = mock(MediaService.class);
        ContextHolderUtil contextHolderUtil = mock(ContextHolderUtil.class);
        when(mediaService.list(5, 0)).thenReturn(new ResponseDTO(5,0, new ArrayList<>()));
        MediaController mediaController = new MediaController(mediaService, contextHolderUtil);
        mediaController.getMedia(999L, 5, 0);
        verify(mediaService).list(5, 0);
    }

    @Test
    public void shouldGetS3ThumbnailUrl() {
        String input = "https://s3.ap-south-1.amazonaws.com/staging-user-media/mt/e70a306c-27c4-43cf-9aaa-37c8d3153dca.jpeg";
        String expectedOutput = "https://s3.ap-south-1.amazonaws.com/staging-user-media/mt/thumbnails/e70a306c-27c4-43cf-9aaa-37c8d3153dca.jpeg";

        String actualOutput = genericUtil.getThumbnailUrl(input);

        assertEquals(expectedOutput, actualOutput);
    }

}
