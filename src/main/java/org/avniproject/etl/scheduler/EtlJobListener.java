package org.avniproject.etl.scheduler;

import org.avniproject.etl.domain.quartz.ScheduledJobRun;
import org.avniproject.etl.domain.result.EtlResult;
import org.avniproject.etl.repository.quartz.ScheduledJobRunRepository;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EtlJobListener implements JobListener {
    private final ScheduledJobRunRepository scheduledJobRunRepository;

    @Autowired
    public EtlJobListener(ScheduledJobRunRepository scheduledJobRunRepository) {
        this.scheduledJobRunRepository = scheduledJobRunRepository;
    }

    @Override
    public String getName() {
        return "ScheduledJobRunPersister";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        JobDetail jobDetail = context.getJobDetail();
        ScheduledJobRun scheduledJobRun = ScheduledJobRun.create(jobDetail, context.getTrigger());
        scheduledJobRunRepository.save(scheduledJobRun);
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        ScheduledJobRun scheduledJobRun = scheduledJobRunRepository.getLastRun(context.getJobDetail().getKey().getName());
        scheduledJobRun.ended(jobException);
        EtlResult result = (EtlResult) context.getResult();
        scheduledJobRun.setSuccess(result.isSuccess());
        scheduledJobRunRepository.save(scheduledJobRun);
    }
}
