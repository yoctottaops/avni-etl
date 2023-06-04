package org.avniproject.etl.contract.backgroundJob;

import java.util.Date;

public class EtlJobLatestStatusResponse {
    private boolean exists;
    private Date lastStartAt;
    private Date lastEndedAt;
    private Date nextStartAt;

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

    public Date getNextStartAt() {
        return nextStartAt;
    }

    public void setNextStartAt(Date nextStartAt) {
        this.nextStartAt = nextStartAt;
    }
}
