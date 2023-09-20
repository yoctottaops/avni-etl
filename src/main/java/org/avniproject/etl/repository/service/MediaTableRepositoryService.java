package org.avniproject.etl.repository.service;

import org.avniproject.etl.config.AmazonClientService;
import org.avniproject.etl.dto.ImageData;
import org.avniproject.etl.dto.MediaDTO;
import org.avniproject.etl.util.Utils;
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

            String thumbnailUrl = Utils.getThumbnailUrl(imageUrl);
            URL signedThumbnailUrl = amazonClientService.generateMediaDownloadUrl(thumbnailUrl);

            return new MediaDTO(
                    rs.getString("uuid"),
                    rs.getString("subject_first_name"),
                    rs.getString("subject_last_name"),
                    imageUrl,
                    rs.getString("concept_name"),
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

    public ImageData setImageData(ResultSet rs) {
        try {
            return new ImageData(rs.getString("uuid"),
                    rs.getString("subject_first_name"),
                    rs.getString("subject_last_name"),
                    rs.getString("image_url"),
                    rs.getString("concept_name"),
                    rs.getString("subject_type_name"),
                    rs.getString("program_name"),
                    rs.getString("encounter_type_name"),
                    rs.getString("sync_parameter_key1"),
                    rs.getString("sync_parameter_key2"),
                    rs.getString("sync_parameter_value1"),
                    rs.getString("sync_parameter_value2"),
                    rs.getString("address"));
        }catch(SQLException e) {
            throw new Error("Error:" + e.getMessage());
        }
    }
}
