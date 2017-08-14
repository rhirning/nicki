package org.mgnl.nicki.mq.consumer;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.helper.JsonHelper;
import org.mgnl.nicki.mq.model.Consumer;
import org.mgnl.nicki.mq.model.ConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerContextListener implements ServletContextListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(ConsumerContextListener.class);
	private ConsumerThread consumerThread;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		boolean startConsumer = DataHelper.booleanOf(Config.getProperty("nicki.mq.consumer.start", "FALSE"));
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
					if (consumer.isStart()) {
						startConsumer(consumer);
					} else {
						LOG.info("MQ consumer not started: " + consumer.getName());
					}
				}
			}
		} else {
			LOG.info("MQ consumer not started: nicki.mq.consumer.start=FALSE");
			
		}
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
