package org.avniproject.etl.dto;

import java.util.List;

public class AddressRequest {
    public String addressLevelType;
    public List<Integer> addressLevelIds;

    public AddressRequest() {
    }

    public AddressRequest(String addressLevelType, List<Integer> addressLevelIds) {
        this.addressLevelType = addressLevelType;
        this.addressLevelIds = addressLevelIds;
    }

    public String getAddressLevelType() {
        return addressLevelType;
    }

    public void setAddressLevelType(String addressLevelType) {
        this.addressLevelType = addressLevelType;
    }

    public List<Integer> getAddressLevelIds() {
        return addressLevelIds;
    }

    public void setAddressLevelIds(List<Integer> addressLevelIds) {
        this.addressLevelIds = addressLevelIds;
    }
}
