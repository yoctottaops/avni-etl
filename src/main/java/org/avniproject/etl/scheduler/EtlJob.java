package org.avniproject.etl.scheduler;

import org.avniproject.etl.config.ScheduledJobConfig;
import org.avniproject.etl.contract.backgroundJob.JobEntityType;
import org.avniproject.etl.service.EtlService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EtlJob implements Job {
    private final EtlService etlService;
    private final ScheduledJobConfig scheduledJobConfig;

    @Autowired
    public EtlJob(EtlService etlService, ScheduledJobConfig scheduledJobConfig) {
        this.etlService = etlService;
        this.scheduledJobConfig = scheduledJobConfig;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        String entityId = scheduledJobConfig.getEntityId(jobDetail);
        if (jobDataMap.get(ScheduledJobConfig.ENTITY_TYPE).equals(JobEntityType.Organisation))
            context.setResult(etlService.runFor(entityId));
        else
            context.setResult(etlService.runForOrganisationGroup(entityId));
    }
}
