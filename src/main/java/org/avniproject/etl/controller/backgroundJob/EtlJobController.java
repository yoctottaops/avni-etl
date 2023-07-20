package org.avniproject.etl.controller.backgroundJob;

import org.apache.log4j.Logger;
import org.avniproject.etl.config.ScheduledJobConfig;
import org.avniproject.etl.contract.JobScheduleRequest;
import org.avniproject.etl.contract.backgroundJob.EtlJobHistoryItem;
import org.avniproject.etl.contract.backgroundJob.EtlJobStatus;
import org.avniproject.etl.contract.backgroundJob.EtlJobSummary;
import org.avniproject.etl.contract.backgroundJob.JobEntityType;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.avniproject.etl.repository.OrganisationRepository;
import org.avniproject.etl.scheduler.EtlJob;
import org.avniproject.etl.service.backgroundJob.ScheduledJobService;
import org.avniproject.etl.util.DateTimeUtil;
import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.JobDetailImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.avniproject.etl.config.ScheduledJobConfig.SYNC_JOB_GROUP;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@RestController
public class EtlJobController {
    private final Scheduler scheduler;
    private final ScheduledJobConfig scheduledJobConfig;
    private final OrganisationRepository organisationRepository;
    private final ScheduledJobService scheduledJobService;
    private static final Logger logger = Logger.getLogger(EtlJobController.class);

    @Autowired
    public EtlJobController(Scheduler scheduler, ScheduledJobConfig scheduledJobConfig, OrganisationRepository organisationRepository, ScheduledJobService scheduledJobService) {
        this.scheduler = scheduler;
        this.scheduledJobConfig = scheduledJobConfig;
        this.organisationRepository = organisationRepository;
        this.scheduledJobService = scheduledJobService;
    }

    @PreAuthorize("hasAnyAuthority('admin')")
    @GetMapping("/job/{id}")
    public ResponseEntity getJob(@PathVariable String id) throws SchedulerException {
        EtlJobSummary latestJobRun = scheduledJobService.getLatestJobRun(id);
        if (latestJobRun == null)
            return ResponseEntity.notFound().build();
        return new ResponseEntity(latestJobRun, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('admin')")
    @PostMapping("/job/status")
    public List<EtlJobStatus> getStatuses(@RequestBody List<String> organisationUUIDs) {
        return scheduledJobService.getJobs(organisationUUIDs);
    }

    @PreAuthorize("hasAnyAuthority('admin')")
    @GetMapping("/job/history/{id}")
    public List<EtlJobHistoryItem> getJobHistory(@PathVariable String id) {
        return scheduledJobService.getJobHistory(id);
    }

    @PreAuthorize("hasAnyAuthority('admin')")
    @PostMapping("/job")
    public ResponseEntity createJob(@RequestBody JobScheduleRequest jobScheduleRequest) throws SchedulerException {
        OrganisationIdentity organisationIdentity = null;
        List<OrganisationIdentity> organisationIdentitiesInGroup = new ArrayList<>();
        if (jobScheduleRequest.getJobEntityType().equals(JobEntityType.Organisation))
            organisationIdentity = organisationRepository.getOrganisation(jobScheduleRequest.getEntityUUID());
        else
            organisationIdentitiesInGroup = organisationRepository.getOrganisationGroup(jobScheduleRequest.getEntityUUID());

        if (organisationIdentity == null && organisationIdentitiesInGroup.size() == 0)
            return ResponseEntity.badRequest().body(String.format("No such organisation or group exists: %s", jobScheduleRequest.getEntityUUID()));

        EtlJobSummary latestJobRun = scheduledJobService.getLatestJobRun(jobScheduleRequest.getEntityUUID());
        if (latestJobRun != null)
            return ResponseEntity.badRequest().body("Job already present");

        JobDetailImpl jobDetail = new JobDetailImpl();
        jobDetail.setJobClass(EtlJob.class);
        jobDetail.setDurability(true);
        jobDetail.setKey(scheduledJobConfig.getJobKey(jobScheduleRequest.getEntityUUID()));
        jobDetail.setGroup(SYNC_JOB_GROUP);
        jobDetail.setName(jobScheduleRequest.getEntityUUID());
        JobDataMap jobDataMap = scheduledJobConfig.createJobData(jobScheduleRequest.getJobEntityType());
        jobDetail.setJobDataMap(jobDataMap);
        scheduler.addJob(jobDetail, false);

        Trigger trigger = newTrigger()
                .withIdentity(scheduledJobConfig.getTriggerKey(jobScheduleRequest.getEntityUUID()))
                .forJob(jobDetail)
                .withSchedule(simpleSchedule().withIntervalInMinutes(scheduledJobConfig.getRepeatIntervalInMinutes()).repeatForever())
                .startAt(DateTimeUtil.nowPlusSeconds(5))
                .build();

        scheduler.scheduleJob(trigger);
        logger.info(String.format("Job Scheduled for %s:%s", jobScheduleRequest.getJobEntityType(), jobScheduleRequest.getEntityUUID()));
        return ResponseEntity.ok().body("Job Scheduled!");
    }

    @PreAuthorize("hasAnyAuthority('admin')")
    @DeleteMapping(value = "/job/{id}")
    public String deleteJob(@PathVariable String id) throws SchedulerException {
        boolean jobDeleted = scheduler.deleteJob(scheduledJobConfig.getJobKey(id));
        return jobDeleted ? "Job Deleted" : "Job Not Deleted";
    }

    @PreAuthorize("hasAnyAuthority('admin')")
    @DeleteMapping(value = "/job/ff8ad747-b567-46fe-9075-91d17082de15") // since this is not for production usage kept the url cryptic so that is is not accidentally used
    public void deleteAllJobs() throws SchedulerException {
        scheduler.clear();
    }
}
