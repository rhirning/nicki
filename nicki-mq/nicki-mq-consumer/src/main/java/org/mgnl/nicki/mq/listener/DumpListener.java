
package org.mgnl.nicki.mq.listener;

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


import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.mgnl.nicki.mq.base.NickiMessageListener;
import org.mgnl.nicki.mq.model.Consumer;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving dump events.
 * The class that is interested in processing a dump
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addDumpListener</code> method. When
 * the dump event occurs, that object's appropriate
 * method is invoked.
 *
 * @see DumpEvent
 */
public class DumpListener implements NickiMessageListener {
	
	/** The consumer. */
	private Consumer consumer;

	/**
	 * On message.
	 *
	 * @param message the message
	 */
	@Override
	public void onMessage(Message message) {

		try {
        	Map<String, String> propertyMap = new HashMap<>();
        	@SuppressWarnings("unchecked")
			Enumeration<String> names = message.getPropertyNames();
        	while (names.hasMoreElements()) {
        		String key = names.nextElement();
        		propertyMap.put(key, message.getStringProperty(key));        		
        	}
        	System.out.println("Consumer: " + consumer.getName() + ", message: properties=" + propertyMap + ", " + ((TextMessage) message).getText());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Sets the consumer.
	 *
	 * @param consumer the new consumer
	 */
	@Override
	public void setConsumer(Consumer consumer) {
		this.consumer = consumer;
	}

}
