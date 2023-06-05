package org.avniproject.etl.domain.quartz;

import jakarta.persistence.*;
import org.avniproject.etl.util.ExceptionUtil;
import org.quartz.JobDetail;
import org.quartz.JobExecutionException;
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

    @Column
    private String errorMessage;

    public static ScheduledJobRun create(JobDetail jobDetail, Trigger trigger) {
        ScheduledJobRun scheduledJobRun = new ScheduledJobRun();
        scheduledJobRun.jobName = jobDetail.getKey().getName();
        scheduledJobRun.startedAt = trigger.getStartTime();
        return scheduledJobRun;
    }

    public void ended(JobExecutionException jobException) {
        this.endedAt = new Date();
        errorMessage = ExceptionUtil.getStackTraceAsString(jobException);
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
