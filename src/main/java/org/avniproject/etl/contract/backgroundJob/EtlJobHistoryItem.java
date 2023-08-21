package org.avniproject.etl.contract.backgroundJob;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class EtlJobHistoryItem {
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Date startedAt;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Date endedAt;
    private String errorMessage;
    private boolean success;

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Date endedAt) {
        this.endedAt = endedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean getSuccess() {
        return success;
    }
}
