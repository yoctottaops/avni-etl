package org.avniproject.etl.domain.quartz;

import jakarta.persistence.*;
import org.quartz.JobDetail;
import org.quartz.Trigger;

import java.util.Date;

@Entity
@Table(name = "scheduled_job_run")
public class ScheduledJobRun {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    @Id
    private Long id;

    @Column
    private String jobName;

    @Column
    private Date startedAt;

    @Column
    private Date endedAt;

    public static ScheduledJobRun create(JobDetail jobDetail, Trigger trigger) {
        ScheduledJobRun scheduledJobRun = new ScheduledJobRun();
        scheduledJobRun.jobName = jobDetail.getKey().getName();
        scheduledJobRun.startedAt = trigger.getStartTime();
        return scheduledJobRun;
    }

    public void setEndedAt(Date endedAt) {
        this.endedAt = endedAt;
    }

    public Long getId() {
        return id;
    }

    public String getJobName() {
        return jobName;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public Date getEndedAt() {
        return endedAt;
    }
}
