package org.avniproject.etl.service.backgroundJob;

import org.avniproject.etl.config.ScheduledJobConfig;
import org.avniproject.etl.contract.backgroundJob.EtlJobSummary;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Service
public class ScheduledJobService {
    private final JdbcTemplate jdbcTemplate;
    private final Scheduler scheduler;
    private final ScheduledJobConfig scheduledJobConfig;

    @Autowired
    public ScheduledJobService(JdbcTemplate jdbcTemplate, Scheduler scheduler, ScheduledJobConfig scheduledJobConfig) {
        this.jdbcTemplate = jdbcTemplate;
        this.scheduler = scheduler;
        this.scheduledJobConfig = scheduledJobConfig;
    }

    public EtlJobSummary getLatestJobRun(String organisationUUID) throws SchedulerException {
        String query = "select sjr.started_at, sjr.ended_at, sjr.error_message from qrtz_job_details qjd\n" +
                "    left outer join scheduled_job_run sjr on sjr.job_name = qjd.job_name\n" +
                "     where sjr.job_name = ?" +
                "order by 1 desc\n" +
                "limit 1";
        List<EtlJobSummary> summaries = jdbcTemplate.query(query, ps -> ps.setString(1, organisationUUID), new EtlJobLatestStatusResponseMapper());
        if (summaries.size() == 0) return null;
        EtlJobSummary etlJobSummary = summaries.get(0);

        JobDetail jobDetail = scheduler.getJobDetail(scheduledJobConfig.getJobKey(organisationUUID));
        etlJobSummary.setCreatedAt((Date) jobDetail.getJobDataMap().get(ScheduledJobConfig.JOB_CREATED_AT));
        return etlJobSummary;
    }

    static class EtlJobLatestStatusResponseMapper implements RowMapper<EtlJobSummary> {
        @Override
        public EtlJobSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
            EtlJobSummary etlJobLatestStatusResponse = new EtlJobSummary();
            etlJobLatestStatusResponse.setLastStartAt(rs.getDate(1));
            etlJobLatestStatusResponse.setLastEndedAt(rs.getDate(2));
            etlJobLatestStatusResponse.setErrorMessage(rs.getString(3));
            return etlJobLatestStatusResponse;
        }
    }
}
