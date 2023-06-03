package org.avniproject.etl.repository.quartz;

import org.avniproject.etl.domain.quartz.ScheduledJobRun;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduledJobRunRepository extends CrudRepository<ScheduledJobRun, Long> {
    ScheduledJobRun findFirstByJobNameOrderByIdDesc(String jobName);

    default ScheduledJobRun getLastRun(String jobName) {
        return findFirstByJobNameOrderByIdDesc(jobName);
    }

    List<ScheduledJobRun> getLatestRuns();
}
