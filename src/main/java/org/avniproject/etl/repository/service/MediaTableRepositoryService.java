package org.avniproject.etl.repository.service;

import org.avniproject.etl.config.AmazonClientService;
import org.avniproject.etl.dto.MediaDTO;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class MediaTableRepositoryService {

    private final AmazonClientService amazonClientService;

    public MediaTableRepositoryService(AmazonClientService amazonClientService) {
        this.amazonClientService = amazonClientService;
    }


    public MediaDTO setMediaDto(ResultSet rs) {

        try {
            String imageUrl = rs.getString("image_url");
            URL signedImageUrl = amazonClientService.generateMediaDownloadUrl(imageUrl);

            String thumbnailUrl = getThumbnailUrl(imageUrl);
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


    public String getThumbnailUrl (String imageUrl) {
        String[] parts = imageUrl.split("/", 4);
        String bucketName = parts[2];
        String objectKey = parts[3];

        int slashIndex = objectKey.lastIndexOf('/');

        String folderPath = "";

        if (slashIndex != -1) {
            folderPath = objectKey.substring(0, slashIndex + 1);
            objectKey = objectKey.substring(slashIndex + 1);
        }

        String thumbnailUrl = "https://" + bucketName + "/" + folderPath + "thumbnails/" + objectKey;
        return thumbnailUrl;
    }
}
