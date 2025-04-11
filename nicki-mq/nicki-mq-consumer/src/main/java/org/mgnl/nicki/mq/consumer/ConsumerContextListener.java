
package org.mgnl.nicki.mq.consumer;

/*-
 * #%L
 * nicki-mq
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
import java.util.Map;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.helper.JsonHelper;
import org.mgnl.nicki.mq.model.Consumer;
import org.mgnl.nicki.mq.model.ConsumerConfig;
import org.mgnl.nicki.verify.Verify;
import org.mgnl.nicki.verify.VerifyException;

import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * ConsumerContextListener.
 *
 * @author B996043
 * 
 * Konfiguration:
 * nicki.mq.consumer.start = true  	==> die Consumer werden gestartet
 * nicki.mq.consumer.config			==> Konfigurationsdatei 
 */
@Slf4j
public class ConsumerContextListener implements ServletContextListener {
	
	/** The consumer threads. */
	private Map<String,ConsumerThread> consumerThreads = new HashMap<>();

	/**
	 * Context initialized.
	 *
	 * @param sce the sce
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		boolean startConsumer = Config.getBoolean("nicki.mq.consumer.start", false);
		if (startConsumer) {
			String configPath = sce.getServletContext().getInitParameter("mq.config");
			if (StringUtils.isBlank(configPath)) {
				configPath = Config.getString("nicki.mq.consumer.config");
			}
			start(configPath);
		} else {
			log.info("MQ consumer not started: nicki.mq.consumer.start=FALSE");
			
		}
	}
	
	/**
	 * Start consumers.
	 *
	 * @param configPath the config path
	 * @return the consumer context listener
	 */
	public static ConsumerContextListener startConsumers(String configPath) {
		ConsumerContextListener consumerContextListener = new ConsumerContextListener();
		consumerContextListener.start(configPath);
		return consumerContextListener;
	}
	
	/**
	 * Start.
	 *
	 * @param configPath the config path
	 */
	private void start(String configPath) {
		ConsumerConfig consumerConfig = null;
		try {
			consumerConfig = JsonHelper.toBean(ConsumerConfig.class, getClass().getResourceAsStream(configPath));
		} catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
			log.error("Error readin condumer config: " + configPath, e);
		}
		if (consumerConfig != null) {
			for (Consumer consumer : consumerConfig.getConsumers()) {
				if (isStart(consumer)) {
					startConsumer(consumer);
				} else {
					log.info("MQ consumer not started: " + consumer);
				}
			}
		}
		
	}

	
	/**
	 * Checks if is start.
	 *
	 * @param consumer the consumer
	 * @return true, if is start
	 */
	private boolean isStart(Consumer consumer) {
		if (StringUtils.isNotBlank(consumer.getStart())) {
			return DataHelper.booleanOf(consumer.getStart());
		}
		if (StringUtils.isNotBlank(consumer.getRule())) {
			String value = DataHelper.translate(StringUtils.substringBefore(consumer.getRule(), ":"));
			String rule = StringUtils.substringAfter(consumer.getRule(), ":");
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
	 * Start consumer.
	 *
	 * @param consumer the consumer
	 */
	private void startConsumer(Consumer consumer) {
		consumerThreads.put(consumer.getName(), new ConsumerThread(consumer));
		consumerThreads.get(consumer.getName()).start();
		log.info("MQ consumer started: " + consumer);
	}
	
	/**
	 * Stop all consumers.
	 */
	public void stopAllConsumers() {
		for (String key : consumerThreads.keySet()) {
			consumerThreads.get(key).setStop(true);
			log.info("MQ consumer stopped: " + key);
			
		}
	}

	/**
	 * Context destroyed.
	 *
	 * @param sce the sce
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		stopAllConsumers();
	}

}
