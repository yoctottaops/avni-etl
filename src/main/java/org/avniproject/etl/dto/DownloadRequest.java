package org.avniproject.etl.dto;

import java.util.List;

public record DownloadRequest(String username,
                              String description,
                              List<AddressLevelType> addressLevelTypes,
                              List<ImageData> data) {
}
