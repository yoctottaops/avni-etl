package org.avniproject.etl.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import org.avniproject.etl.dto.MediaSearchRequest;
import org.avniproject.etl.repository.sql.Page;
import org.avniproject.etl.service.MediaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MediaController {
    private final MediaService mediaService;

    MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PreAuthorize("hasAnyAuthority('analytics_user')")
    @GetMapping("/media")
    public ResponseEntity list(HttpServletRequest request,
                                      @PathParam("size") int size,
                                      @PathParam("page") int page) {
        return search(request, new MediaSearchRequest(), size, page);
    }

    @PreAuthorize("hasAnyAuthority('analytics_user')")
    @PostMapping("/media/search")
    public ResponseEntity search(HttpServletRequest request,
                                 @RequestBody(required = false) MediaSearchRequest mediaSearchRequest,
                                 @PathParam("size") int size,
                                 @PathParam("page") int page) {
        try {
            MediaSearchRequest modifiedRequest = mediaSearchRequest;
            if (mediaSearchRequest == null)
                modifiedRequest = new MediaSearchRequest();
            return ResponseEntity.ok(mediaService.search(modifiedRequest, new Page(page, size)));
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }
}
