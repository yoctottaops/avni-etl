package org.avniproject.etl.scheduler;

import org.avniproject.etl.config.ScheduledJobConfig;
import org.avniproject.etl.service.EtlService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
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
        String organisationId = scheduledJobConfig.getOrganisationId(context.getJobDetail());
        etlService.runForOrganisation(organisationId);
    }
}
