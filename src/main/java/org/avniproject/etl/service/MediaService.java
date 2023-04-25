package org.avniproject.etl.service;

import org.avniproject.etl.dto.MediaSearchRequest;
import org.avniproject.etl.dto.ResponseDTO;
import org.avniproject.etl.repository.MediaTableRepository;
import org.avniproject.etl.repository.sql.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MediaService {

    @Autowired
    private final MediaTableRepository mediaTableRepository;

    MediaService(MediaTableRepository mediaTableRepository){
        this.mediaTableRepository = mediaTableRepository;
    }

    @Transactional (readOnly = true)
    public ResponseDTO search(MediaSearchRequest mediaSearchRequest, Page page) {
        return new ResponseDTO(page, mediaTableRepository.search(mediaSearchRequest, page));
    }
}
