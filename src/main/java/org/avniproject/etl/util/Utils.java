package org.avniproject.etl.util;

import org.springframework.stereotype.Service;

@Service
public class Utils {
    public static String getThumbnailUrl (String imageUrl) {
        int slashIndex = imageUrl.lastIndexOf('/');

        String firstPart = "";
        String objectKey = "";

        if (slashIndex != -1) {
            firstPart = imageUrl.substring(0, slashIndex + 1);
            objectKey = imageUrl.substring(slashIndex + 1);
        }

        return firstPart + "thumbnails/" + objectKey;
    }
}
