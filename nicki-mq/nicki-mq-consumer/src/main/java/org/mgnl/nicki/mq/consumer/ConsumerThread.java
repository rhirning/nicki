
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


import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.mq.base.NickiMessageListener;
import org.mgnl.nicki.mq.model.Consumer;

import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class ConsumerThread.
 */
@Slf4j
public class ConsumerThread extends Thread implements Runnable {
	
	/** The Constant DEFAULT_CHARSET. */
	public final static String DEFAULT_CHARSET = "UTF-8";
	
	/** The connection. */
	private Connection connection = null;
	
	/** The session. */
	private Session session = null;
	
	/** The message consumer. */
	private MessageConsumer messageConsumer;
	
	/** The stop. */
	private boolean stop;
	
	/** The consumer. */
	private Consumer consumer;
	
	/**
	 * Instantiates a new consumer thread.
	 *
	 * @param consumer the consumer
	 */
	public ConsumerThread(Consumer consumer) {
		super();
		this.consumer = consumer;
	}

	/**
	 * Run.
	 */
	public void run() {
		try {
			ConnectionFactory factory = new ActiveMQConnectionFactory(Config.getString(consumer.getBase() + ".connector"));
			connection = factory.createConnection("consumer", Config.getString(consumer.getBase() + ".user.password.consumer"));
			connection.start();

			session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

			Destination destination = session.createQueue(consumer.getDestination());
			String selector = DataHelper.translate(consumer.getSelector());
			messageConsumer = session.createConsumer(destination, selector);
			NickiMessageListener messageListener = Classes.newInstance(consumer.getListener());
			messageListener.setConsumer(consumer);
			messageConsumer.setMessageListener(messageListener);
			
		} catch (JMSException | ClassNotFoundException | InstantiationException | IllegalAccessException e0) {
			log.error("Error starting MQConsumerListener", e0);
			if (messageConsumer != null) {
				try {
					messageConsumer.close();
				} catch (JMSException e) {
					log.error("Error closing consumer", e);
				}
			}
			if (session != null) {
				try {
					session.close();
				} catch (JMSException e) {
					log.error("Error closing session", e);
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					log.error("Error closing connextion", e);
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
					log.error("Error closing consumer", e);
				}
			}
			if (session != null) {
				try {
					session.close();
				} catch (JMSException e) {
					log.error("Error closing session", e);
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					log.error("Error closing connextion", e);
				}
			}
		}
	}

	/**
	 * Sets the stop.
	 *
	 * @param stop the new stop
	 */
	public void setStop(boolean stop) {
		this.stop = stop;
	}
}
