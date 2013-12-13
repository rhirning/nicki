package org.mgnl.nicki.cache.filter;

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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class CacheFilter implements Filter {

	List<Pattern> patterns;
	private CacheManager cacheManager;
	private Cache cache;

	private static final Logger LOG = LoggerFactory
			.getLogger(CacheFilter.class);
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
		LOG.debug("Config file:" + configFile);

		String cacheName = filterConfig.getInitParameter(CACHE_NAME);
		if (StringUtils.isBlank(cacheName)) {
			cacheName = "nickiCache";
		}
		LOG.debug("Cache name:" + cacheName);

		patterns = new ArrayList<Pattern>();
		String patternString = filterConfig.getInitParameter(PATTERN);
		LOG.debug("Pattern: " + patternString);
		if (StringUtils.isNotBlank(patternString)) {
			String pArray[] = StringUtils.split(patternString,
					PATTERN_SEPARATOR);
			if (pArray != null && pArray.length > 0) {
				for (int i = 0; i < pArray.length; i++) {
					LOG.debug("add pattern: " + pArray[i]);
					patterns.add(Pattern.compile(pArray[i]));
				}
			}
		}

		cacheManager = CacheManager.create(CacheFilter.class
				.getResource(configFile));

		cache = cacheManager.getCache(cacheName);

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
		Element element = cache.get(cacheKey);
		if (element != null) {
			CachedEntry cachedEntry = (CachedEntry) element.getObjectValue();
			cachedEntry.replay(response);
			LOG.debug("Delivered from Cache: " + cacheKey);
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

		CachedEntry cachedEntry = makeCachedEntry(responseWrapper);

		if (cachedEntry == null) {
			// put null to unblock the cache
			cache.put(new Element(cacheKey, null));
			return;
		}

		cachedEntry.replay(response);

		LOG.debug("Write to Cache: " + cacheKey);
		cache.put(new Element(cacheKey, cachedEntry));

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
