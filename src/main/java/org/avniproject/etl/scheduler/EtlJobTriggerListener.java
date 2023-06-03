package org.avniproject.etl.scheduler;

import org.avniproject.etl.domain.quartz.ScheduledJobRun;
import org.avniproject.etl.repository.quartz.ScheduledJobRunRepository;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EtlJobTriggerListener implements TriggerListener {
    private final ScheduledJobRunRepository scheduledJobRunRepository;

    @Autowired
    public EtlJobTriggerListener(ScheduledJobRunRepository scheduledJobRunRepository) {
        this.scheduledJobRunRepository = scheduledJobRunRepository;
    }

    @Override
    public String getName() {
        return "ScheduledJobRunPersister";
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        JobDetail jobDetail = context.getJobDetail();
        ScheduledJobRun scheduledJobRun = ScheduledJobRun.create(jobDetail, trigger);
        scheduledJobRunRepository.save(scheduledJobRun);
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {
        ScheduledJobRun scheduledJobRun = scheduledJobRunRepository.getLastRun(context.getJobDetail().getKey().getName());
        scheduledJobRun.setEndedAt(trigger.getEndTime());
        scheduledJobRunRepository.save(scheduledJobRun);
    }
}
