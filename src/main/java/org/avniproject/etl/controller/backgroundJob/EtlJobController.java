package org.avniproject.etl.controller.backgroundJob;

import org.avniproject.etl.config.ScheduledJobConfig;
import org.avniproject.etl.contract.JobScheduleRequest;
import org.avniproject.etl.contract.backgroundJob.EtlJobSummary;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.avniproject.etl.repository.OrganisationRepository;
import org.avniproject.etl.scheduler.EtlJob;
import org.avniproject.etl.service.backgroundJob.ScheduledJobService;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static org.avniproject.etl.config.ScheduledJobConfig.SYNC_JOB_GROUP;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@RestController
public class EtlJobController {
    private final Scheduler scheduler;
    private final ScheduledJobConfig scheduledJobConfig;
    private final OrganisationRepository organisationRepository;
    private final ScheduledJobService scheduledJobService;

    @Autowired
    public EtlJobController(Scheduler scheduler, ScheduledJobConfig scheduledJobConfig, OrganisationRepository organisationRepository, ScheduledJobService scheduledJobService) {
        this.scheduler = scheduler;
        this.scheduledJobConfig = scheduledJobConfig;
        this.organisationRepository = organisationRepository;
        this.scheduledJobService = scheduledJobService;
    }

    @GetMapping("/etl/job/{id}")
    public ResponseEntity getJob(@PathVariable String id) throws SchedulerException {
        EtlJobSummary latestJobRun = scheduledJobService.getLatestJobRun(id);
        if (latestJobRun == null)
            return ResponseEntity.notFound().build();
        return new ResponseEntity(latestJobRun, HttpStatus.OK);
    }

    @PostMapping("/etl/job")
    public ResponseEntity createJob(@RequestBody JobScheduleRequest jobScheduleRequest) throws SchedulerException {
        OrganisationIdentity organisation = organisationRepository.getOrganisation(jobScheduleRequest.getOrganisationUUID());
        if (organisation == null)
            return ResponseEntity.badRequest().body(String.format("No such organisation exists: %s", jobScheduleRequest.getOrganisationUUID()));

        JobDetailImpl jobDetail = new JobDetailImpl();
        jobDetail.setJobClass(EtlJob.class);
        jobDetail.setDurability(true);
        jobDetail.setKey(scheduledJobConfig.getJobKey(jobScheduleRequest.getOrganisationUUID()));
        jobDetail.setGroup(SYNC_JOB_GROUP);
        jobDetail.setName(jobScheduleRequest.getOrganisationUUID());
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduledJobConfig.JOB_CREATED_AT, new Date());
        jobDetail.setJobDataMap(jobDataMap);
        scheduler.addJob(jobDetail, false);

        Trigger trigger = newTrigger()
                .withIdentity(scheduledJobConfig.getTriggerKey(jobScheduleRequest.getOrganisationUUID()))
                .forJob(jobDetail)
                .withSchedule(simpleSchedule().withIntervalInMinutes(scheduledJobConfig.getRepeatIntervalInMinutes()).repeatForever())
                .build();

        scheduler.scheduleJob(trigger);
        return ResponseEntity.ok().body("Job Scheduled!");
    }

    @DeleteMapping(value = "/etl/job/{id}")
    public String deleteJob(@PathVariable String id) throws SchedulerException {
        boolean jobDeleted = scheduler.deleteJob(scheduledJobConfig.getJobKey(id));
        return jobDeleted ? "Job Deleted" : "Job Not Deleted";
    }
}
