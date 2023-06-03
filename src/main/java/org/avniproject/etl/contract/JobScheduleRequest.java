package org.avniproject.etl.contract;

public class JobScheduleRequest {
    private String organisationUUID;

    public String getOrganisationUUID() {
        return organisationUUID;
    }

    public void setOrganisationUUID(String organisationUUID) {
        this.organisationUUID = organisationUUID;
    }
}
