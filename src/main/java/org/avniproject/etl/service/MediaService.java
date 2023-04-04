package org.avniproject.etl.service;

import org.avniproject.etl.dto.MediaDTO;
import org.avniproject.etl.dto.ResponseDTO;
import org.avniproject.etl.repository.MediaTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MediaService {

    @Autowired
    private final MediaTableRepository mediaTableRepository;

    MediaService(MediaTableRepository mediaTableRepository){
        this.mediaTableRepository = mediaTableRepository;
    }


    /**
     * @param schemaName
     * @param size
     * @param page
     * @return
     */
    @Transactional
    public ResponseDTO list(String schemaName,int size, int page){
        int total = mediaTableRepository.findTotalMedia(schemaName);
        List<MediaDTO> mediaList= mediaTableRepository.findAll(schemaName, size, page);
        return new ResponseDTO(total, page, mediaList);
    }

}
