package org.mgnl.nicki.mq.broker;

/*-
 * #%L
 * nicki-mq-broker
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
import java.util.HashMap;

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

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.verify.Verify;
import org.mgnl.nicki.verify.VerifyException;

import lombok.extern.slf4j.Slf4j;
/**
 * ActiveMQContextListener
 * startet einen ActiveMQ Broker
 * @author rhirning
 * 
 * Konfiguration:
 * nicki.mq.broker.start				= boolean
 * nicki.mq.broker.rule					= Regeln gemäß org.mgnl.nicki.verify.Verify. Beispiel: #{hostname}:values:lxtom17p
 * nicki.mq.broker.wait					= Startverzögerung in Sekunden (z.B. 30)
 * nicki.mq.connector					= URL des Brokers (z.B tcp://lxtom17p.pnw.loc:61616)
 * nicki.mq.persistenceType				= JDBC
 * nicki.mq.context						= nicki-db-context (z.B. history)
 * nicki.mq.tables.create				= boolean
 * nicki.mq.user.password.producer		= Producer Passwort
 * nicki.mq.user.password.consumer		= Consumer Passwort
 * nicki.mq.user.password.admin			= Admin Passwort
 * 
 */
@Slf4j
public class ActiveMQContextListener implements ServletContextListener {
	private BrokerThread brokerThread;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		if (isStartBroker()) {
			log.info("Load ActiveMQ");
			brokerThread = new BrokerThread();
			brokerThread.start();
		} else {
			log.info("ActiveMQ not started");			
		}
	}

	
	private boolean isStartBroker() {
		if (Config.exists("nicki.mq.broker.start")) {
			return Config.getBoolean("nicki.mq.broker.start", false);
		}
		if (Config.exists("nicki.mq.broker.rule")) {
			String ruleString = Config.getString("nicki.mq.broker.rule");
			String value = DataHelper.translate(StringUtils.substringBefore(ruleString, ":"));
			String rule = StringUtils.substringAfter(ruleString, ":");
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
		if (brokerThread != null) {
			try {
				log.info("ActiveMQ exiting");
				brokerThread.setStop(true);
				log.info("ActiveMQ exit succesfully");
			} catch (Exception e) {
				log.error("Unable to exit ActiveMQ!", e);
			}
		}
	}

}
