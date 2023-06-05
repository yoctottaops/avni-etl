package org.avniproject.etl;

import org.avniproject.etl.scheduler.EtlJobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@SpringBootApplication
//Pesky little thing runs every time an integration test is added. This property is the only thing
//that can turn it off
@ConditionalOnProperty(
		prefix = "application.runner",
		value = "enabled",
		havingValue = "true",
		matchIfMissing = true)
public class EtlApplication {
    @Autowired
    public EtlApplication(Scheduler scheduler, EtlJobListener etlJobListener) throws SchedulerException {
        scheduler.getListenerManager().addJobListener(etlJobListener);
        scheduler.start();
    }

    public static void main(String[] args) {
	    SpringApplication.run(EtlApplication.class, args);
	}
}
