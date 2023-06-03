package org.avniproject.etl.contract.backgroundJob;

public class EtlJobResponse {
    private boolean exists;

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public boolean getExists() {
        return exists;
    }
}
