package org.avniproject.etl.dto;

import java.util.List;

public class DownloadAllMediaRequest {
    private MediaSearchRequest mediaSearchRequest;
    private String username;
    private String description;
    private List<AddressLevelType> addressLevelTypes;

    public DownloadAllMediaRequest (MediaSearchRequest mediaSearchRequest, String username, String description, List<AddressLevelType> addressLevelTypes){
        this.mediaSearchRequest = mediaSearchRequest;
        this.username = username;
        this.description = description;
        this.addressLevelTypes = addressLevelTypes;
    }

    public void setMediaSearchRequest(MediaSearchRequest mediaSearchRequest) {
        this.mediaSearchRequest = mediaSearchRequest;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAddressLevelTypes(List<AddressLevelType> addressLevelTypes) {
        this.addressLevelTypes = addressLevelTypes;
    }

    public MediaSearchRequest getMediaSearchRequest() {
        return  (mediaSearchRequest == null) ? new MediaSearchRequest(): mediaSearchRequest;
    }

    public String getUsername() {
        return username;
    }

    public String getDescription() {
        return description;
    }

    public List<AddressLevelType> getAddressLevelTypes() {
        return addressLevelTypes;
    }
}
