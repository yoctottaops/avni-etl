package org.avniproject.etl.scheduler;

import org.avniproject.etl.service.EtlService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
public class EtlJob implements Job {
    private final EtlService etlService;

    @Autowired
    public EtlJob(EtlService etlService) {
        this.etlService = etlService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        etlService.run();
    }
}
