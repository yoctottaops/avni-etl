package org.avniproject.etl.controller.backgroundJob;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EtlSchedulerController {
    private Scheduler scheduler;

    @Autowired
    public EtlSchedulerController(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @GetMapping("/etl/scheduler")
    public boolean getSchedulerStatus() throws SchedulerException {
        return scheduler.isStarted();
    }
}
