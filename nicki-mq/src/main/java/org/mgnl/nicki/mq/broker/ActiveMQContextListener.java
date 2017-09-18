
package org.mgnl.nicki.mq.broker;

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


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveMQContextListener implements ServletContextListener {
	private static final Logger LOG = LoggerFactory.getLogger(ActiveMQContextListener.class);
	private BrokerThread brokerThread;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		boolean startBroker = DataHelper.booleanOf(Config.getProperty("nicki.mq.broker.start", "FALSE"));
		if (startBroker) {
			LOG.info("Load ActiveMQ");
			BrokerThread brokerThread = new BrokerThread();
			brokerThread.start();
		} else {
			LOG.info("ActiveMQ not started");			
		}
	}


	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if (brokerThread != null) {
			try {
				LOG.info("ActiveMQ exiting");
				brokerThread.setStop(true);
				LOG.info("ActiveMQ exit succesfully");
			} catch (Exception e) {
				LOG.error("Unable to exit ActiveMQ!", e);
			}
		}
	}

}
