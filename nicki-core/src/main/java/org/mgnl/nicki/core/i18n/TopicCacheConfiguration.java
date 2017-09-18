
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
