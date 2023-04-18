package org.avniproject.etl.repository.service;

import org.avniproject.etl.config.AmazonClientService;
import org.avniproject.etl.dto.MediaDTO;
import org.avniproject.etl.util.Utils;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class MediaTableRepositoryService {

    private final AmazonClientService amazonClientService;

    private final Utils utils;

    public MediaTableRepositoryService(AmazonClientService amazonClientService, Utils utils) {
        this.amazonClientService = amazonClientService;
        this.utils = utils;
    }


    public MediaDTO setMediaDto(ResultSet rs) {

        try {
            String imageUrl = rs.getString("image_url");
            URL signedImageUrl = amazonClientService.generateMediaDownloadUrl(imageUrl);

            String thumbnailUrl = utils.getThumbnailUrl(imageUrl);
            URL signedThumbnailUrl = amazonClientService.generateMediaDownloadUrl(thumbnailUrl);

            return new MediaDTO(
                    rs.getString("uuid"),
                    imageUrl,
                    signedImageUrl,
                    thumbnailUrl,
                    signedThumbnailUrl,
                    rs.getString("subject_type_name"),
                    rs.getString("program_name"),
                    rs.getString("encounter_type_name"),
                    rs.getString("last_modified_date_time"),
                    rs.getString("created_date_time"),
                    rs.getString("sync_parameter_key1"),
                    rs.getString("sync_parameter_key2"),
                    rs.getString("sync_parameter_value1"),
                    rs.getString("sync_parameter_value2"),
                    rs.getString("address")
            );
        } catch (SQLException e) {
            throw new Error("Error:" + e.getMessage());
        }

    }


}
