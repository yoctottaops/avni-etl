package org.avniproject.etl.config;

import org.avniproject.etl.contract.backgroundJob.JobEntityType;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduledJobConfig {
    public static final String SYNC_JOB_GROUP = "SyncJobs";
    public static final String SYNC_TRIGGER_GROUP = "SyncTriggers";
    public static final String JOB_CREATED_AT = "CreatedAt";
    public static final String ENTITY_TYPE = "EntityType";

    @Value("${avni.scheduledJob.repeatIntervalInMinutes}")
    private int repeatIntervalInMinutes;

    public TriggerKey getTriggerKey(String organisationUUID) {
        return new TriggerKey(organisationUUID, SYNC_TRIGGER_GROUP);
    }

    public int getRepeatIntervalInMinutes() {
        return repeatIntervalInMinutes;
    }

    public JobKey getJobKey(String organisationUUID) {
        return new JobKey(organisationUUID, SYNC_JOB_GROUP);
    }

    public String getEntityId(JobDetail jobDetail) {
        return jobDetail.getKey().getName();
    }

    public JobDataMap createJobData(JobEntityType jobEntityType) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduledJobConfig.JOB_CREATED_AT, new Date());
        jobDataMap.put(ScheduledJobConfig.ENTITY_TYPE, jobEntityType);
        return jobDataMap;
    }
}
