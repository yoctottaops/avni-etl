package org.avniproject.etl.contract;

import org.avniproject.etl.contract.backgroundJob.JobEntityType;

public class JobScheduleRequest {
    private String entityUUID;
    private JobEntityType jobEntityType;

    public String getEntityUUID() {
        return entityUUID;
    }

    public void setEntityUUID(String entityUUID) {
        this.entityUUID = entityUUID;
    }

    public JobEntityType getJobEntityType() {
        return jobEntityType;
    }

    public void setJobEntityType(JobEntityType jobEntityType) {
        this.jobEntityType = jobEntityType;
    }
}
