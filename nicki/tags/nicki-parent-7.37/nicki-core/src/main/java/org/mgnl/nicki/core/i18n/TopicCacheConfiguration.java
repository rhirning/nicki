package org.mgnl.nicki.core.i18n;

import org.mgnl.nicki.core.util.Classes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopicCacheConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(TopicCacheConfiguration.class);
	private String topic;
	private CacheEntryFactory factory;
	private boolean valid;
	
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public CacheEntryFactory getFactory() {
		return factory;
	}
	public void setFactoryClass(String factoryClass) {
		try {
			this.factory = Classes.newInstance(factoryClass);
			this.valid = true;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			LOG.error("Error creating CacheFactory " + factoryClass, e);
		}
	}
	public boolean isValid() {
		return valid;
	}

}
