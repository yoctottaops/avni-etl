package org.avniproject.etl.controller;

import jakarta.websocket.server.PathParam;
import org.avniproject.etl.config.ContextHolderUtil;
import org.avniproject.etl.dto.MediaDTO;
import org.avniproject.etl.dto.ResponseDTO;
import org.avniproject.etl.service.MediaService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MediaController {

    private final MediaService mediaService;

    MediaController(MediaService mediaService){
        this.mediaService = mediaService;
    }

    @GetMapping ("/media")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseDTO<MediaDTO> getMedia(@PathParam("schemaName") String schemaName,
                                          @PathParam("db") String db,
                                          @PathParam("size") int size,
                                          @PathParam("page") int page) {
        ContextHolderUtil.setParameters(schemaName, db);
        return mediaService.list(schemaName, size, page);
    }
}
