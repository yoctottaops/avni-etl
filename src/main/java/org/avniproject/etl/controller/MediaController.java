package org.avniproject.etl.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.avniproject.etl.dto.MediaSearchRequest;
import org.avniproject.etl.repository.OrganisationRepository;
import org.avniproject.etl.repository.sql.Page;
import org.avniproject.etl.security.UserContext;
import org.avniproject.etl.service.AuthService;
import org.avniproject.etl.service.MediaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MediaController {
    private final MediaService mediaService;
    private final AuthService authService;
    private final OrganisationRepository organisationRepository;

    MediaController(MediaService mediaService, AuthService authService, OrganisationRepository organisationRepository) {
        this.mediaService = mediaService;
        this.authService = authService;
        this.organisationRepository = organisationRepository;
    }

    @GetMapping("/media")
    public ResponseEntity list(HttpServletRequest request,
                                      @PathParam("size") int size,
                                      @PathParam("page") int page) {
        return search(request, new MediaSearchRequest(), size, page);
    }

    @PostMapping("/media/search")
    public ResponseEntity search(HttpServletRequest request,
                                 @RequestBody MediaSearchRequest mediaSearchRequest,
                                 @PathParam("size") int size,
                                 @PathParam("page") int page) {
        String token = request.getHeader("AUTH-TOKEN");
        UserContext userContext =  authService.authenticateByToken(token);
        OrganisationIdentity organisationIdentity = organisationRepository.getOrganisationByUser(userContext.getUser());
        ContextHolder.setContext(organisationIdentity);
        try {
            return ResponseEntity.ok(mediaService.search(mediaSearchRequest, new Page(page, size)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }
}
