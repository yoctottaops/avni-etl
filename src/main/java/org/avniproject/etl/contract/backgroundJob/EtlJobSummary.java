package org.avniproject.etl.contract.backgroundJob;

import java.util.Date;

public class EtlJobSummary {
    private Date createdAt;
    private Date lastStartAt;
    private Date lastEndedAt;
    private String errorMessage;

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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
