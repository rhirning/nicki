package org.mgnl.nicki.spnego;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraceHttpFilter implements Filter {
	private static final Logger LOG = LoggerFactory.getLogger(TraceHttpFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		LOG.info("starting filter");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		LOG.info("filtering");
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			System.out.println(httpServletRequest.getPathInfo());
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

}
