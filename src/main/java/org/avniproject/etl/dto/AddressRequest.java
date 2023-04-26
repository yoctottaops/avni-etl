package org.avniproject.etl.dto;

import java.util.List;

public class AddressRequest {
    public String addressLevelType;
    public List<Integer> addressIds;

    public AddressRequest() {
    }

    public AddressRequest(String addressLevelType, List<Integer> addressIds) {
        this.addressLevelType = addressLevelType;
        this.addressIds = addressIds;
    }

    public String getAddressLevelType() {
        return addressLevelType;
    }

    public void setAddressLevelType(String addressLevelType) {
        this.addressLevelType = addressLevelType;
    }

    public List<Integer> getAddressIds() {
        return addressIds;
    }

    public void setAddressIds(List<Integer> addressIds) {
        this.addressIds = addressIds;
    }
}
