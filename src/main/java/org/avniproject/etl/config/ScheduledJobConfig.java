package org.avniproject.etl.config;

import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ScheduledJobConfig {
    public static final String SYNC_JOB_GROUP = "SyncJobs";
    public static final String SYNC_TRIGGER_GROUP = "SyncTriggers";

    @Value("${avni.scheduledJob.repeatIntervalInMinutes}")
    private int repeatIntervalInMinutes;

    public TriggerKey getTriggerKey(String organisationUUID) {
        return new TriggerKey(organisationUUID, SYNC_TRIGGER_GROUP);
    }

    public int getRepeatIntervalInMinutes() {
        return repeatIntervalInMinutes;
    }

    public JobKey getJobKey(String organisationUUID) {
        return new JobKey(organisationUUID);
    }
}
