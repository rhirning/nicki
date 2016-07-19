/**
 * Copyright (c) 2003-2015 Dr. Ralf Hirning
 * All rights reserved.
 *  
 * This program is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 * 
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU Public License v3.0
 * which is available at
 * http://www.gnu.org/licenses/gpl.html
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 * 
 * Any modifications to this file must keep this entire header
 * intact.
*/
package org.mgnl.nicki.template.engine;

import java.util.HashMap;
import java.util.Map;

import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.template.loader.ClassPathTemplateLoader;
import org.mgnl.nicki.template.loader.JndiTemplateLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;

public class ConfigurationFactory {
	private static final Logger LOG = LoggerFactory.getLogger(ConfigurationFactory.class);
	
	public enum TYPE {JNDI, CLASSPATH};

	private static ConfigurationFactory instance = new ConfigurationFactory();
	private Map<String, Configuration> configurations = new HashMap<String, Configuration>();
	public Configuration getConfiguration(TYPE type, String base) {
		String key = type.name() + "-" + base;
		if (!configurations.containsKey(key)) {
			Configuration cfg = new Configuration();
			cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
			TemplateLoader loader = null;
			if (type == TYPE.JNDI) {
				try {
					loader = new JndiTemplateLoader(AppContext.getSystemContext(), base);
				} catch (InvalidPrincipalException e) {
					LOG.error("Error initiating JNDI templates", e);
				}
			} else if (type == TYPE.CLASSPATH) {
				loader = new ClassPathTemplateLoader(base);
			}
			if (loader != null) {
				cfg.setTemplateLoader(loader);
				configurations.put(key, cfg);
			}
		}
		return configurations.get(key);
	}

	public Configuration getConfiguration(String baseDn) throws InvalidPrincipalException {
		return getConfiguration(TYPE.JNDI, baseDn);
	}

	public static ConfigurationFactory getInstance() {
		return instance;
	}
}
