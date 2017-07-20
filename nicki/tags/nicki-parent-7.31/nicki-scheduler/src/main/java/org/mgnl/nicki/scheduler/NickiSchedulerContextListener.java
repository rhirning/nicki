package org.mgnl.nicki.scheduler;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.helper.JsonHelper;
import org.mgnl.nicki.core.util.Classes;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NickiSchedulerContextListener implements ServletContextListener {
	private static final Logger LOG = LoggerFactory.getLogger(NickiSchedulerContextListener.class);

	private Scheduler scheduler;
	
	public NickiSchedulerContextListener() {
		super();
	    LOG.info("------- Initializing -------------------");
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {

		ServletContext ctx = servletContextEvent.getServletContext();
		String configPath = ctx.getInitParameter("jobConfig");
		if (StringUtils.isNotBlank(configPath)) {
			try {
				JobConfigurations jobConfigurations = JsonHelper.toBean(JobConfigurations.class,
						getClass().getResourceAsStream(configPath));
				// Grab the Scheduler instance from the Factory
				scheduler = StdSchedulerFactory.getDefaultScheduler();

				if (jobConfigurations.getJobConfig() != null) {
					for (JobConfig syncConfig : jobConfigurations.getJobConfig()) {
						if (syncConfig.isActive()) {
							Job jobClass = Classes.newInstance(syncConfig.getJobClassName());
							JobDetail job = JobBuilder.newJob(jobClass.getClass()).withIdentity(syncConfig.getName(), syncConfig.getGroup()).build();

						    CronTrigger trigger = newTrigger().withIdentity(syncConfig.getName(), syncConfig.getGroup()).withSchedule(cronSchedule(syncConfig.getCronSchedule()))
						        .build();

						    Date ft = scheduler.scheduleJob(job, trigger);
						    LOG.info(job.getKey() + " has been scheduled to run at: " + ft + " and repeat based on expression: "
						             + trigger.getCronExpression());
						}
					}
				}
				// and start it off
				scheduler.start();
			} catch (SchedulerException | IllegalAccessException | InvocationTargetException | InstantiationException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	    LOG.info("------- Shutting Down ---------------------");

	    try {
			scheduler.shutdown(true);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    LOG.info("------- Shutdown Complete -----------------");

	    SchedulerMetaData metaData;
		try {
			metaData = scheduler.getMetaData();
		    LOG.info("Executed " + metaData.getNumberOfJobsExecuted() + " jobs.");
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
