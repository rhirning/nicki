
package org.mgnl.nicki.cache.filter;

/*-
 * #%L
 * nicki-cache
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


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheFilter implements Filter {

	List<Pattern> patterns;
	private CacheManager cacheManager;
	private Cache<String, CachedEntry> cache;

	private static final String CONFIG = "config";
	private static final String CACHE_NAME = "cacheName";
	private static final String PATTERN = "pattern";
	private static final String PATTERN_SEPARATOR = "|";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String configFile = filterConfig.getInitParameter(CONFIG);
		if (StringUtils.isBlank(configFile)) {
			configFile = "/config/ehcache.xml";
		}
		// DOMConfigurator.configure("log4j.xml");
		log.debug("Config file:" + configFile);

		String cacheName = filterConfig.getInitParameter(CACHE_NAME);
		if (StringUtils.isBlank(cacheName)) {
			cacheName = "nickiCache";
		}
		log.debug("Cache name:" + cacheName);

		patterns = new ArrayList<Pattern>();
		String patternString = filterConfig.getInitParameter(PATTERN);
		log.debug("Pattern: " + patternString);
		if (StringUtils.isNotBlank(patternString)) {
			String pArray[] = StringUtils.split(patternString,
					PATTERN_SEPARATOR);
			if (pArray != null && pArray.length > 0) {
				for (int i = 0; i < pArray.length; i++) {
					log.debug("add pattern: " + pArray[i]);
					patterns.add(Pattern.compile(pArray[i]));
				}
			}
		}

		cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
				.withCache(cacheName,
						CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, CachedEntry.class, ResourcePoolsBuilder.heap(10)))
				.build();
				

		cache = cacheManager.getCache(cacheName, String.class, CachedEntry.class);

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		// requested Object
		String cacheKey = ((HttpServletRequest) request).getPathInfo();

		// is cache configured for this object
		boolean useCache = false;
		if (StringUtils.isNotBlank(cacheKey)) {
			for (Pattern pattern : patterns) {
				Matcher matcher = pattern.matcher(cacheKey);
				if (matcher.matches()) {
					useCache = true;
				}
			}
		}

		if (!useCache) {
			chain.doFilter(req, resp);
			return;
		}

		// is cached Object available?
		CachedEntry cachedEntry = cache.get(cacheKey);
		if (cachedEntry != null) {
			cachedEntry.replay(response);
			log.debug("Delivered from Cache: " + cacheKey);
			return;
		}

		final CacheResponseWrapper responseWrapper = new CacheResponseWrapper(
				response);

		final long cacheStorageDate = System.currentTimeMillis();
		responseWrapper.setDateHeader("Last-Modified", cacheStorageDate);

		chain.doFilter(request, responseWrapper);

		if (responseWrapper.getStatus() == HttpServletResponse.SC_NOT_MODIFIED) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		}

		cachedEntry = makeCachedEntry(responseWrapper);

		if (cachedEntry == null) {
			// put null to unblock the cache
			cache.put(cacheKey, null);
			return;
		}

		cachedEntry.replay(response);

		log.debug("Write to Cache: " + cacheKey);
		cache.put(cacheKey, cachedEntry);

	}

	protected CachedEntry makeCachedEntry(CacheResponseWrapper cachedResponse)
			throws IOException {

		CachedEntry cachedEntry = new CachedEntry(
				cachedResponse.getBufferedContent(),
				cachedResponse.getContentType(),
				cachedResponse.getCharacterEncoding(),
				cachedResponse.getStatus(), cachedResponse.getHeaders());

		return cachedEntry;
	}

	@Override
	public void destroy() {
	}

}
