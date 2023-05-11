package org.avniproject.etl.repository;

import org.avniproject.etl.dto.MediaDTO;
import org.avniproject.etl.dto.MediaSearchRequest;
import org.avniproject.etl.repository.service.MediaTableRepositoryService;
import org.avniproject.etl.repository.sql.MediaSearchQueryBuilder;
import org.avniproject.etl.repository.sql.Page;
import org.avniproject.etl.repository.sql.Query;
import org.avniproject.etl.service.CognitoAuthServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.avniproject.etl.repository.JdbcContextWrapper.runInOrgContext;

@Repository
public class MediaTableRepository {

    private final JdbcTemplate jdbcTemplate;
    private final MediaTableRepositoryService mediaTableRepositoryService;
    private final Logger log = LoggerFactory.getLogger(MediaTableRepository.class);

    @Autowired
    MediaTableRepository(JdbcTemplate jdbcTemplate, MediaTableRepositoryService mediaTableRepositoryService) {
        this.jdbcTemplate = jdbcTemplate;
        this.mediaTableRepositoryService = mediaTableRepositoryService;
    }

    public List<MediaDTO> search(MediaSearchRequest mediaSearchRequest, Page page) {
        Query query = new MediaSearchQueryBuilder()
                .withPage(page)
                .withMediaSearchRequest(mediaSearchRequest)
                .build();
        return runInOrgContext(() -> new NamedParameterJdbcTemplate(jdbcTemplate)
                .query(query.sql(), query.parameters(),
                        (rs, rowNum) -> mediaTableRepositoryService.setMediaDto(rs)), jdbcTemplate);
    }
}
