package org.mgnl.nicki.scheduler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

public class TestJob implements Job {

    private static Logger LOG = LoggerFactory.getLogger(TestJob.class);

    public TestJob() {
    }

    public void execute(JobExecutionContext context)
        throws JobExecutionException {

        // This job simply prints out its job name and the
        // date and time that it is running
        JobKey jobKey = context.getJobDetail().getKey();
        LOG.info(getClass().getSimpleName() + " says: " + jobKey + " executing at " + new Date());
    }

}
