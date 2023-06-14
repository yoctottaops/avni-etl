package org.avniproject.etl.controller.backgroundJob;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EtlSchedulerController {
    private final Scheduler scheduler;

    @Autowired
    public EtlSchedulerController(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @GetMapping("/scheduler")
    public Map<String, Boolean> getSchedulerStatus() throws SchedulerException {
        return new HashMap<>() {{
            put("Started", scheduler.isStarted());
        }};
    }
}
