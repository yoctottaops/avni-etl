package org.avniproject.etl.service;

import org.avniproject.etl.dto.MediaSearchRequest;
import org.avniproject.etl.dto.ResponseDTO;
import org.avniproject.etl.repository.AddressRepository;
import org.avniproject.etl.repository.MediaTableRepository;
import org.avniproject.etl.repository.sql.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MediaService {

    private final MediaTableRepository mediaTableRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public MediaService(MediaTableRepository mediaTableRepository, AddressRepository addressRepository) {
        this.mediaTableRepository = mediaTableRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional (readOnly = true)
    public ResponseDTO search(MediaSearchRequest mediaSearchRequest, Page page) {
        if (addressRepository.doAllAddressLevelTypeNamesExist(mediaSearchRequest.getAddressLevelTypes())) {
            return new ResponseDTO(page, mediaTableRepository.search(mediaSearchRequest, page));
        }

        return null;
    }
}
