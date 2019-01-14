
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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.helper.JsonHelper;
import org.mgnl.nicki.mq.model.Consumer;
import org.mgnl.nicki.mq.model.ConsumerConfig;
import org.mgnl.nicki.verify.Verify;
import org.mgnl.nicki.verify.VerifyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsumerContextListener implements ServletContextListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(ConsumerContextListener.class);
	private ConsumerThread consumerThread;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		boolean startConsumer = Config.getBoolean("nicki.mq.consumer.start", false);
		if (startConsumer) {
			String configPath = sce.getServletContext().getInitParameter("mq.config");
			ConsumerConfig consumerConfig = null;
			try {
				consumerConfig = JsonHelper.toBean(ConsumerConfig.class, getClass().getResourceAsStream(configPath));
			} catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
				LOG.error("Error readin condumer config: " + configPath, e);
			}
			if (consumerConfig != null) {
				for (Consumer consumer : consumerConfig.getConsumers()) {
					if (isStart(consumer)) {
						startConsumer(consumer);
					} else {
						LOG.info("MQ consumer not started: " + consumer);
					}
				}
			}
		} else {
			LOG.info("MQ consumer not started: nicki.mq.consumer.start=FALSE");
			
		}
	}

	
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


	private void startConsumer(Consumer consumer) {
		consumerThread = new ConsumerThread(consumer);
		consumerThread.start();
		LOG.info("MQ consumer started: " + consumer);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if (this.consumerThread != null) {
			this.consumerThread.setStop(true);
		}
	}

}
