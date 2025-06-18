
package org.mgnl.nicki.core.i18n;

/*-
 * #%L
 * nicki-core
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


import org.mgnl.nicki.core.util.Classes;

import lombok.extern.slf4j.Slf4j;


/**
 * The Class TopicCacheConfiguration.
 */
@Slf4j
public class TopicCacheConfiguration {
	
	/** The topic. */
	private String topic;
	
	/** The factory. */
	private CacheEntryFactory factory;
	
	/** The valid. */
	private boolean valid;
	
	/**
	 * Gets the topic.
	 *
	 * @return the topic
	 */
	public String getTopic() {
		return topic;
	}
	
	/**
	 * Sets the topic.
	 *
	 * @param topic the new topic
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	/**
	 * Gets the factory.
	 *
	 * @return the factory
	 */
	public CacheEntryFactory getFactory() {
		return factory;
	}
	
	/**
	 * Sets the factory class.
	 *
	 * @param factoryClass the new factory class
	 */
	public void setFactoryClass(String factoryClass) {
		try {
			this.factory = Classes.newInstance(factoryClass);
			this.valid = true;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			log.error("Error creating CacheFactory " + factoryClass, e);
		}
	}
	
	/**
	 * Checks if is valid.
	 *
	 * @return true, if is valid
	 */
	public boolean isValid() {
		return valid;
	}

}
