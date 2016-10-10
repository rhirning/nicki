package org.mgnl.nicki.core.helper;

import java.io.Serializable;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class Environment implements Serializable{
	
	private static final Logger LOG = LoggerFactory.getLogger(Environment.class);
	
	public static String getProperty(String name) {
		if (getFromJndiContext(name) != null) {
			return getFromJndiContext(name);
		}
		if (getFromSystemProperty(name) != null) {
			return getFromSystemProperty(name);
		}
		if (getFromSystemEnvironment(name) != null) {
			return getFromSystemEnvironment(name);
		}
		
		return null;
	}

	public static String getFromJndiContext(String name) {
		// Check JNDI environment
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			String value = (String) envCtx.lookup(name);
			if (StringUtils.isNotBlank(value)) {
				return value;
			}
		} catch (Exception e) {
			LOG.debug("Error accessing JNDI: " + name, e);
		}
		return null;
	}

	public static String getFromSystemProperty(String name) {
		try {
			return System.getProperty(name);
		} catch (Exception e) {
			LOG.debug("Error accessing System.getProperty: " + name, e);
		}
		return null;
	}

	public static String getFromSystemEnvironment(String name) {
		try {
			return System.getenv(name);
		} catch (Exception e) {
			LOG.debug("Error accessing System.getenv: " + name, e);
		}
		return null;
	}

}
