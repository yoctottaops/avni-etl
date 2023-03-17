package org.avniproject.etl.scheduler;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@Configuration
public class EtlConfig {
    private static String ETL_JOB_ID = "ETL";

    @Bean
    public JobDetail etlJobDetail() {
        return JobBuilder.newJob().ofType(EtlJob.class)
                .storeDurably()
                .withIdentity(ETL_JOB_ID)
                .build();
    }

    @Bean
    public Trigger trigger(JobDetail job) {
        return TriggerBuilder.newTrigger().forJob(job)
                .withIdentity(ETL_JOB_ID)
                .withSchedule(simpleSchedule().repeatForever().withIntervalInHours(1))
                .startNow()
                .build();
    }
}
