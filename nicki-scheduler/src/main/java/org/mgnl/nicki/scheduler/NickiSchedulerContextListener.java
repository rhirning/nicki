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



import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.helper.JsonHelper;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.verify.Verify;
import org.mgnl.nicki.verify.VerifyException;

import it.sauronsoftware.cron4j.Scheduler;
import lombok.extern.slf4j.Slf4j;


/**
 * NickiSchedulerContextListener.
 *
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

	/** The scheduler. */
	private Scheduler scheduler;
	
	/**
	 * Instantiates a new nicki scheduler context listener.
	 */
	public NickiSchedulerContextListener() {
		super();
	    log.info("------- Initializing -------------------");
	}

	/**
	 * Context initialized.
	 *
	 * @param servletContextEvent the servlet context event
	 */
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
				scheduler = new Scheduler();
	
				if (jobConfigurations.getJobConfig() != null) {
					for (JobConfig syncConfig : jobConfigurations.getJobConfig()) {
						if (isActive(syncConfig)) {
							Job job = Classes.newInstance(syncConfig.getJobClassName());
							job.setJobConfig(syncConfig);
							scheduler.schedule(syncConfig.getCronSchedule(), job);
						    log.info(job.getJobConfig().getName() + " has been scheduled: " + syncConfig);
						} else {
						    log.info("Job has not been scheduled to run: " + syncConfig);
						}
					}
				}
				// and start it off
				scheduler.start();
			} catch (IllegalAccessException | InvocationTargetException | InstantiationException | ClassNotFoundException  e) {
				log.error("Error scheduling jobs", e);

			}

		}

	}

	/**
	 * Checks if is active.
	 *
	 * @param syncConfig the sync config
	 * @return true, if is active
	 */
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

	/**
	 * Context destroyed.
	 *
	 * @param sce the sce
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		log.info("------- Shutting Down ---------------------");
		scheduler.stop();
	    log.info("------- Shutdown Complete -----------------");
	}

}
