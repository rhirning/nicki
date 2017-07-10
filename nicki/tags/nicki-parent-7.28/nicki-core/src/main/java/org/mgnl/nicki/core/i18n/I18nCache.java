package org.mgnl.nicki.core.i18n;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cache with topic and EntryFactory
 * 
 * 
 * @author rhirning
 *
 */
public class I18nCache {
    private static final Logger LOG = LoggerFactory.getLogger(I18nCache.class);
	private static I18nCache instance = new I18nCache();
	private Map<String, TopicCacheConfiguration> caches = new HashMap<>();
	private CacheManager cacheManager;

	public I18nCache() {
		cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();
		String configFile = Config.getProperty("nicki.i18n.cache.config");
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
			LOG.error("Error reading cache configuration from " + configFile, e);
		}
	}

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
	
	public static String getText(String topic, String key) {
		return instance.getTranslatedText(topic, key);
	}


	public static String getText(String topic, String key, String ... data) {
		return instance.getTranslatedText(topic, key, data);
	}
	

}
