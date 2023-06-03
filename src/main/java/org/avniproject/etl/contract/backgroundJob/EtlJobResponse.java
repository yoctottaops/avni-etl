package org.avniproject.etl.contract.backgroundJob;

import java.util.Date;

public class EtlJobResponse {
    private boolean exists;
    private Date lastStartAt;
    private Date lastEndedAt;

    public boolean isExists() {
        return exists;
    }

    public Date getLastStartAt() {
        return lastStartAt;
    }

    public void setLastStartAt(Date lastStartAt) {
        this.lastStartAt = lastStartAt;
    }

    public Date getLastEndedAt() {
        return lastEndedAt;
    }

    public void setLastEndedAt(Date lastEndedAt) {
        this.lastEndedAt = lastEndedAt;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }
}
