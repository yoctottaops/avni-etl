package org.avniproject.etl.dto;

import java.util.List;

public class SyncValue {
    private String syncConceptName;
    private List<String> syncConceptValues;

    public SyncValue(String syncConceptName, List<String> syncConceptValues) {
        this.syncConceptName = syncConceptName;
        this.syncConceptValues = syncConceptValues;
    }

    public SyncValue() {
    }

    public String getSyncConceptName() {
        return syncConceptName;
    }

    public void setSyncConceptName(String syncConceptName) {
        this.syncConceptName = syncConceptName;
    }

    public List<String> getSyncConceptValues() {
        return syncConceptValues;
    }

    public void setSyncConceptValues(List<String> syncConceptValues) {
        this.syncConceptValues = syncConceptValues;
    }
}
