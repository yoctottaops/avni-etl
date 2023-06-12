package org.avniproject.etl.contract.backgroundJob;

public class EtlJobStatus {
    private String entityUUID;
    private boolean analyticsEnabled;

    public String getEntityUUID() {
        return entityUUID;
    }

    public void setEntityUUID(String entityUUID) {
        this.entityUUID = entityUUID;
    }

    public boolean isAnalyticsEnabled() {
        return analyticsEnabled;
    }

    public void setAnalyticsEnabled(boolean analyticsEnabled) {
        this.analyticsEnabled = analyticsEnabled;
    }
}
