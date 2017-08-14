package org.mgnl.nicki.core.i18n;

import org.ehcache.Cache;

public interface CacheEntryFactory {

	void load(String key, Cache<String, String> cache);

}
