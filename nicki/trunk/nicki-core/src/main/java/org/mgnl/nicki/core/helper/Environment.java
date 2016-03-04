package org.mgnl.nicki.core.helper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class Environment implements Serializable{
	
	private static final Logger LOG = LoggerFactory.getLogger(Environment.class);
	private static Environment instance;
	private Map<String, String> map;
	
	public Environment() {
		this.map = new HashMap<>();
	}

	public static void setProperty(String name, String value) {
		getInstance()._put(name, value);
	}
	
	public static String getProperty(String name) {
		if (getFromSystemEnvironment(name) != null) {
			return getFromSystemEnvironment(name);
		}
		if (getPropertyFromSystemEnvironment(name) != null) {
			return getPropertyFromSystemEnvironment(name);
		}
		if (getFromJndiContext(name) != null) {
			return getFromJndiContext(name);
		}
		if (contains(name)) {
			return getInstance()._get(name);
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

	public static String getPropertyFromSystemEnvironment(String name) {
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

	public static boolean contains(String name) {
		if (instance == null) {
			return false;
		}
		return getInstance()._contains(name);
	}

	private boolean _contains(String name) {
		return this.map.containsKey(name);
	}

	private String _get(String name) {
		return this.map.get(name);
	}

	private void _put(String name, String value) {
		this.map.put(name, value);
	}

	private synchronized static Environment getInstance() {
		if (instance == null) {
			instance = new Environment();
		}
		return instance;
	}

}
