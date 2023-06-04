package org.avniproject.etl.contract.backgroundJob;

import java.util.Date;

public class EtlJobHistoryResponse {
    private Date startedAt;
    private Date endedAt;

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
}
