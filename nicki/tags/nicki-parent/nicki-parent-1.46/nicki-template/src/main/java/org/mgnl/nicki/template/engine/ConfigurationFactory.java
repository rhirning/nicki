/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.template.engine;

import java.util.HashMap;
import java.util.Map;

import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.ldap.context.AppContext;
import org.mgnl.nicki.template.loader.JndiTemplateLoader;

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;

public class ConfigurationFactory {

	private static ConfigurationFactory instance = new ConfigurationFactory();
	private Map<String, Configuration> configurations = new HashMap<String, Configuration>();
	public Configuration getConfiguration(String baseDn) throws InvalidPrincipalException {
		if (!configurations.containsKey(baseDn)) {
			JndiTemplateLoader loader = new JndiTemplateLoader(AppContext.getSystemContext(), baseDn);
			Configuration cfg = new Configuration();
			cfg.setTemplateLoader(loader);
			cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
			configurations.put(baseDn, cfg);
		}
		return configurations.get(baseDn);
	}

	public static ConfigurationFactory getInstance() {
		return instance;
	}
}
