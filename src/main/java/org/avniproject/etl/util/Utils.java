package org.avniproject.etl.util;

import org.springframework.stereotype.Service;

@Service
public class Utils {

    public static String getThumbnailUrl (String imageUrl) {
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
