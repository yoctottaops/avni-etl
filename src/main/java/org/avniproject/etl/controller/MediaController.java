package org.avniproject.etl.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import org.apache.log4j.Logger;
import org.avniproject.etl.config.ContextHolderUtil;
import org.avniproject.etl.dto.MediaDTO;
import org.avniproject.etl.dto.MediaSearchRequest;
import org.avniproject.etl.dto.ResponseDTO;
import org.avniproject.etl.repository.sql.Page;
import org.avniproject.etl.service.EtlService;
import org.avniproject.etl.service.MediaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Pageable;


@RestController
public class MediaController {

    private static final Logger log = Logger.getLogger(MediaController.class);
    private final MediaService mediaService;
    private final ContextHolderUtil contextHolderUtil;

    MediaController(MediaService mediaService, ContextHolderUtil contextHolderUtil) {
        this.mediaService = mediaService;
        this.contextHolderUtil = contextHolderUtil;
    }

    @GetMapping("/media")
    public ResponseDTO<MediaDTO> list(HttpServletRequest request,
                                      @PathParam("size") int size,
                                      @PathParam("page") int page) {
        String token = request.getHeader("AUTH-TOKEN");
        contextHolderUtil.setUser(token);
        return mediaService.list(new Page(page, size));
    }

    @PostMapping("/media/search")
    public ResponseDTO<MediaDTO> search(HttpServletRequest request,
                                        @RequestBody MediaSearchRequest mediaSearchRequest,
                                        @PathParam("size") int size,
                                        @PathParam("page") int page) {
        String token = request.getHeader("AUTH-TOKEN");
        contextHolderUtil.setUser(token);
        return mediaService.search(mediaSearchRequest, new Page(page, size));
    }
}
