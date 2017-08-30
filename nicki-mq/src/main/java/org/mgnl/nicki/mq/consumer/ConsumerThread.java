/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.mq.consumer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.mq.base.NickiMessageListener;
import org.mgnl.nicki.mq.model.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerThread extends Thread implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(ConsumerThread.class);
	public final static String DEFAULT_CHARSET = "UTF-8";
	private Connection connection = null;
	private Session session = null;
	private MessageConsumer messageConsumer;
	private boolean stop;
	private Consumer consumer;
	
	public ConsumerThread(Consumer consumer) {
		super();
		this.consumer = consumer;
	}

	public void run() {
		try {
			ConnectionFactory factory = new ActiveMQConnectionFactory(Config.getProperty(consumer.getBase() + ".connector"));
			connection = factory.createConnection("consumer", Config.getProperty(consumer.getBase() + ".user.password.consumer"));
			connection.start();

			session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

			Destination destination = session.createQueue(consumer.getDestination());
			messageConsumer = session.createConsumer(destination, consumer.getSelector());
			NickiMessageListener messageListener = Classes.newInstance(consumer.getListener());
			messageListener.setConsumer(consumer);
			messageConsumer.setMessageListener(messageListener);
			
		} catch (JMSException | ClassNotFoundException | InstantiationException | IllegalAccessException e0) {
			LOG.error("Error starting MQConsumerListener", e0);
			if (messageConsumer != null) {
				try {
					messageConsumer.close();
				} catch (JMSException e) {
					LOG.error("Error closing consumer", e);
				}
			}
			if (session != null) {
				try {
					session.close();
				} catch (JMSException e) {
					LOG.error("Error closing session", e);
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					LOG.error("Error closing connextion", e);
				}
			}
		}
		try {
			while (true) {
				try {
					Thread.sleep(1000);
					if (stop) {
						break;
					}
				} catch (InterruptedException e) {
					break;
				}
			}
		} finally {
			if (messageConsumer != null) {
				try {
					messageConsumer.close();
				} catch (JMSException e) {
					LOG.error("Error closing consumer", e);
				}
			}
			if (session != null) {
				try {
					session.close();
				} catch (JMSException e) {
					LOG.error("Error closing session", e);
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					LOG.error("Error closing connextion", e);
				}
			}
		}
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}
}
