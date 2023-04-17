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


    MediaController(MediaService mediaService){
        this.mediaService = mediaService;
    }

    @GetMapping ("/media")
    @CrossOrigin(origins = "http://localhost:3000,https://staging.avniproject.org")
    public ResponseDTO<MediaDTO> getMedia(@PathParam("orgID") Long orgID,
                                          @PathParam("size") int size,
                                          @PathParam("page") int page) {
        ContextHolderUtil.setParameters(orgID);
        return mediaService.list(size, page);
    }
}
