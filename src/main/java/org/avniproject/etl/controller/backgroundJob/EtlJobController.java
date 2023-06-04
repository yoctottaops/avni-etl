package org.avniproject.etl.controller.backgroundJob;

import org.avniproject.etl.config.ScheduledJobConfig;
import org.avniproject.etl.contract.JobScheduleRequest;
import org.avniproject.etl.contract.backgroundJob.EtlJobLatestStatusResponse;
import org.avniproject.etl.domain.quartz.ScheduledJobRun;
import org.avniproject.etl.repository.quartz.ScheduledJobRunRepository;
import org.avniproject.etl.scheduler.EtlJob;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.JobDetailImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.avniproject.etl.config.ScheduledJobConfig.SYNC_JOB_GROUP;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@RestController
public class EtlJobController {
    private final Scheduler scheduler;
    private final ScheduledJobConfig scheduledJobConfig;
    private final ScheduledJobRunRepository scheduledJobRunRepository;

    @Autowired
    public EtlJobController(Scheduler scheduler, ScheduledJobConfig scheduledJobConfig, ScheduledJobRunRepository scheduledJobRunRepository) {
        this.scheduler = scheduler;
        this.scheduledJobConfig = scheduledJobConfig;
        this.scheduledJobRunRepository = scheduledJobRunRepository;
    }

    @GetMapping("/etl/job")
    public List<EtlJobLatestStatusResponse> getJobs() {
        List<ScheduledJobRun> latestRuns = scheduledJobRunRepository.getLatestRuns();
        return latestRuns.stream().map(scheduledJobRun -> {
            EtlJobLatestStatusResponse response = new EtlJobLatestStatusResponse();
            response.setExists(true);
            response.setLastStartAt(scheduledJobRun.getStartedAt());
            response.setLastEndedAt(scheduledJobRun.getEndedAt());
            return response;
        }).collect(Collectors.toList());
    }

    @GetMapping("/etl/job/{id}")
    public EtlJobLatestStatusResponse getJob(@PathVariable String id) throws SchedulerException {
        EtlJobLatestStatusResponse etlJobResponse = new EtlJobLatestStatusResponse();
        JobDetail jobDetail = scheduler.getJobDetail(scheduledJobConfig.getJobKey(id));
        if (jobDetail != null) {
            Trigger trigger = scheduler.getTrigger(scheduledJobConfig.getTriggerKey(id));
            etlJobResponse.setExists(true);
            etlJobResponse.setLastStartAt(trigger.getStartTime());
            etlJobResponse.setLastEndedAt(trigger.getEndTime());
            etlJobResponse.setNextStartAt(trigger.getNextFireTime());
        }
        return etlJobResponse;
    }

    @PostMapping("/etl/job")
    public void createJob(JobScheduleRequest jobScheduleRequest) throws SchedulerException {
        JobDetailImpl jobDetail = new JobDetailImpl();
        jobDetail.setJobClass(EtlJob.class);
        jobDetail.setDurability(true);
        jobDetail.setKey(scheduledJobConfig.getJobKey(jobScheduleRequest.getOrganisationUUID()));
        jobDetail.setGroup(SYNC_JOB_GROUP);
        jobDetail.setName(jobScheduleRequest.getOrganisationUUID());
        scheduler.addJob(jobDetail, true);

        Trigger trigger = newTrigger()
                .withIdentity(scheduledJobConfig.getTriggerKey(jobScheduleRequest.getOrganisationUUID()))
                .forJob(jobDetail)
                .withSchedule(simpleSchedule().withIntervalInMinutes(scheduledJobConfig.getRepeatIntervalInMinutes()).repeatForever())
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    @DeleteMapping("/etl/job/${id}")
    public void deleteJob(@PathVariable String id) throws SchedulerException {
        scheduler.deleteJob(scheduledJobConfig.getJobKey(id));
    }
}
