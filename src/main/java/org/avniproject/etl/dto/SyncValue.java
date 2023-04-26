package org.avniproject.etl.dto;

public class SyncValue {
    private String syncConceptName;
    private String syncConceptValue;

    public SyncValue(String syncConceptName, String syncConceptValue) {
        this.syncConceptName = syncConceptName;
        this.syncConceptValue = syncConceptValue;
    }

    public SyncValue() {
    }

    public String getSyncConceptName() {
        return syncConceptName;
    }

    public void setSyncConceptName(String syncConceptName) {
        this.syncConceptName = syncConceptName;
    }

    public String getSyncConceptValue() {
        return syncConceptValue;
    }

    public void setSyncConceptValue(String syncConceptValue) {
        this.syncConceptValue = syncConceptValue;
    }
}
