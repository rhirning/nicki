
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


import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.JsonHelper;

import lombok.extern.slf4j.Slf4j;


/**
 * Cache with topic and EntryFactory.
 *
 * @author rhirning
 */
@Slf4j
public class I18nCache {
	
	/** The instance. */
	private static I18nCache instance = new I18nCache();
	
	/** The caches. */
	private Map<String, TopicCacheConfiguration> caches = new HashMap<>();
	
	/** The cache manager. */
	private CacheManager cacheManager;

	/**
	 * Instantiates a new i 18 n cache.
	 */
	public I18nCache() {
		cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();
		String configFile = Config.getString("nicki.i18n.cache.config");
		try {
			CacheConfiguration cacheConfiguration = JsonHelper.toBean(CacheConfiguration.class, getClass().getResourceAsStream(configFile));
			if (cacheConfiguration != null && cacheConfiguration.getCache() != null) {
				for (TopicCacheConfiguration topicCacheConfiguration : cacheConfiguration.getCache()) {
					caches.put(topicCacheConfiguration.getTopic(), topicCacheConfiguration);
					cacheManager.createCache(topicCacheConfiguration.getTopic(),
							CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class,
									ResourcePoolsBuilder.heap(100)).build());
				}
			}
		} catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
			log.error("Error reading cache configuration from " + configFile, e);
		}
	}

	/**
	 * Gets the translated text.
	 *
	 * @param topic the topic
	 * @param key the key
	 * @return the translated text
	 */
	private String getTranslatedText(String topic, String key) {
		if (StringUtils.isNotBlank(topic) && caches.containsKey(topic)) {
			if (!cacheManager.getCache(topic, String.class, String.class).containsKey(key)) {
				CacheEntryFactory factory = caches.get(topic).getFactory();
				factory.load(key, cacheManager.getCache(topic, String.class, String.class));
			}
			if (cacheManager.getCache(topic, String.class, String.class).containsKey(key)) {
				return cacheManager.getCache(topic, String.class, String.class).get(key);
			}
		}
		return key;
	}

	/**
	 * Gets the translated text.
	 *
	 * @param topic the topic
	 * @param key the key
	 * @param data the data
	 * @return the translated text
	 */
	private String getTranslatedText(String topic, String key, String ... data) {
		if (StringUtils.isNotBlank(topic) && caches.containsKey(topic)) {
			if (!cacheManager.getCache(topic, String.class, String.class).containsKey(key)) {
				CacheEntryFactory factory = caches.get(topic).getFactory();
				factory.load(key, cacheManager.getCache(topic, String.class, String.class));
			}
			if (cacheManager.getCache(topic, String.class, String.class).containsKey(key)) {
				return cacheManager.getCache(topic, String.class, String.class).get(key);
			}
		}
		return key;
	}
	
	/**
	 * Gets the text.
	 *
	 * @param topic the topic
	 * @param key the key
	 * @return the text
	 */
	public static String getText(String topic, String key) {
		return instance.getTranslatedText(topic, key);
	}


	/**
	 * Gets the text.
	 *
	 * @param topic the topic
	 * @param key the key
	 * @param data the data
	 * @return the text
	 */
	public static String getText(String topic, String key, String ... data) {
		return instance.getTranslatedText(topic, key, data);
	}
	

}
