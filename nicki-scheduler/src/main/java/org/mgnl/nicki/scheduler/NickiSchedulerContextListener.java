package org.mgnl.nicki.scheduler;

/*-
 * #%L
 * nicki-scheduler
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.helper.JsonHelper;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.verify.Verify;
import org.mgnl.nicki.verify.VerifyException;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.impl.StdSchedulerFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * NickiSchedulerContextListener
 * @author rhirning
 * 
 * Konfigurationsdatei wird festgelegt in
 * 
 * nicki.scheduler.config
 * 
 * ist keine Konfigurationsdatei konfiguriert, dann wird der Scheduler nicht gestartet
 */
@Slf4j
public class NickiSchedulerContextListener implements ServletContextListener {

	private Scheduler scheduler;
	
	public NickiSchedulerContextListener() {
		super();
	    log.info("------- Initializing -------------------");
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {

		ServletContext ctx = servletContextEvent.getServletContext();
		String configPath = ctx.getInitParameter("jobConfig");
		if (StringUtils.isBlank(configPath)) {
			configPath = Config.getString("nicki.scheduler.config");
		}
		if (StringUtils.isNotBlank(configPath)) {
			try {
				JobConfigurations jobConfigurations = JsonHelper.toBean(JobConfigurations.class,
						getClass().getResourceAsStream(configPath));
				// Grab the Scheduler instance from the Factory
				scheduler = StdSchedulerFactory.getDefaultScheduler();

				if (jobConfigurations.getJobConfig() != null) {
					for (JobConfig syncConfig : jobConfigurations.getJobConfig()) {
						if (isActive(syncConfig)) {
							Job jobClass = Classes.newInstance(syncConfig.getJobClassName());
							JobDetail job = JobBuilder.newJob(jobClass.getClass()).withIdentity(syncConfig.getName(), syncConfig.getGroup()).build();

						    CronTrigger trigger = newTrigger().withIdentity(syncConfig.getName(), syncConfig.getGroup()).withSchedule(cronSchedule(syncConfig.getCronSchedule()))
						        .build();

						    Date ft = scheduler.scheduleJob(job, trigger);
						    log.info(job.getKey() + " has been scheduled to run at: " + ft + " and repeat based on expression: "
						             + trigger.getCronExpression());
						} else {
						    log.info("Job has not been scheduled to run: " + syncConfig);
						}
					}
				}
				// and start it off
				scheduler.start();
			} catch (SchedulerException | IllegalAccessException | InvocationTargetException | InstantiationException | ClassNotFoundException e) {
				log.error("Error scheduling jobs", e);

			}

		}

	}

	private boolean isActive(JobConfig syncConfig) {
		if (StringUtils.isNotBlank(syncConfig.getActive())) {
			return DataHelper.booleanOf(syncConfig.getActive());
		}
		if (StringUtils.isNotBlank(syncConfig.getRule())) {
			String value = DataHelper.translate(StringUtils.substringBefore(syncConfig.getRule(), ":"));
			String rule = StringUtils.substringAfter(syncConfig.getRule(), ":");
			try {
				Verify.verifyRule(rule, value, new HashMap<String, String>());
				return true;
			} catch (VerifyException e) {
				log.debug("Verify: " + e.getMessage());
				return false;
			}
		}
		return false;
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		log.info("------- Shutting Down ---------------------");

	    try {
			scheduler.shutdown(true);
		} catch (SchedulerException e) {
			log.error("Error shutting down", e);
		}

	    log.info("------- Shutdown Complete -----------------");

	    SchedulerMetaData metaData;
		try {
			metaData = scheduler.getMetaData();
			log.info("Executed " + metaData.getNumberOfJobsExecuted() + " jobs.");
		} catch (SchedulerException e) {
			log.error("Error getting statistics", e);
		}
	}

}
