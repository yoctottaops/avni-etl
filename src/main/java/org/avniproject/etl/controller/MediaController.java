package org.avniproject.etl.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import org.avniproject.etl.config.ContextHolderUtil;
import org.avniproject.etl.dto.MediaDTO;
import org.avniproject.etl.dto.ResponseDTO;
import org.avniproject.etl.service.MediaService;
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
    public ResponseDTO<MediaDTO> list(HttpServletRequest request, @PathParam("orgID") Long orgID,
                                      @PathParam("size") int size,
                                      @PathParam("page") int page)
    {

        String token = request.getHeader("AUTH-TOKEN");
        System.out.println("ATUH TOKE IN CONTROLLER __" + token );
        contextHolderUtil.setParameters(orgID);
        contextHolderUtil.setUser(token, "organisationUUIDorganisationUUID");
        return mediaService.list(size, page);
    }
}
