package org.avniproject.etl.controller.backgroundJob;

import org.avniproject.etl.config.ScheduledJobConfig;
import org.avniproject.etl.contract.JobScheduleRequest;
import org.avniproject.etl.contract.backgroundJob.EtlJobResponse;
import org.avniproject.etl.scheduler.EtlJob;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.MutableTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.avniproject.etl.config.ScheduledJobConfig.SYNC_JOB_GROUP;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@RestController
public class EtlJobController {
    private final Scheduler scheduler;
    private final ScheduledJobConfig scheduledJobConfig;

    @Autowired
    public EtlJobController(Scheduler scheduler, ScheduledJobConfig scheduledJobConfig) {
        this.scheduler = scheduler;
        this.scheduledJobConfig = scheduledJobConfig;
    }

    @GetMapping("/etl/job")
    public List<EtlJobResponse> getJobs() throws SchedulerException {
        List<JobExecutionContext> currentlyExecutingJobs = scheduler.getCurrentlyExecutingJobs();
        scheduler.getJobKeys(GroupMatcher.anyGroup()).stream()
                .collect(Collectors.toList());
        return new ArrayList<>();
    }

    @GetMapping("/etl/job/{id}")
    public EtlJobResponse getJob(@PathVariable String organisationUUID) throws SchedulerException {
        EtlJobResponse etlJobResponse = new EtlJobResponse();
        JobDetail jobDetail = scheduler.getJobDetail(scheduledJobConfig.getJobKey(organisationUUID));
        if (jobDetail != null)
            etlJobResponse.setExists(true);
        Trigger trigger = scheduler.getTrigger(scheduledJobConfig.getTriggerKey(organisationUUID));
        return null;
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
