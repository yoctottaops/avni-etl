package org.avniproject.etl.service;

import org.avniproject.etl.dto.ResponseDTO;
import org.avniproject.etl.repository.MediaTableRepository;
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

    /**
     * @param size
     * @param page
     * @return
     */
    @Transactional
    public ResponseDTO list(int size, int page){
        int total = mediaTableRepository.findTotalMedia();
        return new ResponseDTO(total, page, mediaTableRepository.findAll(size, page));
    }

}
