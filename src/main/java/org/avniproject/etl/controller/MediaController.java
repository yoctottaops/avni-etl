package org.avniproject.etl.controller;

import jakarta.websocket.server.PathParam;
import org.avniproject.etl.config.ContextHolderUtil;
import org.avniproject.etl.dto.MediaDTO;
import org.avniproject.etl.dto.ResponseDTO;
import org.avniproject.etl.service.MediaService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MediaController {

    private final MediaService mediaService;

    private final ContextHolderUtil contextHolderUtil;

    MediaController(MediaService mediaService, ContextHolderUtil contextHolderUtil){
        this.mediaService = mediaService;
        this.contextHolderUtil = contextHolderUtil;
    }

    @GetMapping ("/media")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseDTO<MediaDTO> getMedia(@PathParam("orgUUID") String orgUUID,
                                          @PathParam("size") int size,
                                          @PathParam("page") int page) {
        contextHolderUtil.setParameters(orgUUID);
        return mediaService.list(size, page);
    }
}
