package org.mgnl.nicki.mq.listener;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.mgnl.nicki.mq.base.NickiMessageListener;
import org.mgnl.nicki.mq.model.Consumer;

public class DumpListener implements NickiMessageListener {
	private Consumer consumer;

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

	@Override
	public void setConsumer(Consumer consumer) {
		this.consumer = consumer;
	}

}
