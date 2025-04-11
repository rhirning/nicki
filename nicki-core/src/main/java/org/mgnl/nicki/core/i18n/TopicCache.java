
package org.mgnl.nicki.core.i18n;

// TODO: Auto-generated Javadoc
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


/**
 * The Class TopicCache.
 */
public class TopicCache {
	
	/** The topic cache configuration. */
	TopicCacheConfiguration topicCacheConfiguration;

	/**
	 * Instantiates a new topic cache.
	 *
	 * @param topicCacheConfiguration the topic cache configuration
	 */
	public TopicCache(TopicCacheConfiguration topicCacheConfiguration) {
		this.topicCacheConfiguration = topicCacheConfiguration;
	}

	/**
	 * Gets the topic.
	 *
	 * @return the topic
	 */
	public String getTopic() {
		return topicCacheConfiguration.getTopic();
	}

	/**
	 * Gets the translated text.
	 *
	 * @param key the key
	 * @return the translated text
	 */
	public String getTranslatedText(String key) {
		// TODO Auto-generated method stub
		return null;
	}

}
