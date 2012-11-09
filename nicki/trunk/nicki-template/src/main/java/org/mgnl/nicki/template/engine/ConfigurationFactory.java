/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.template.engine;

import java.util.HashMap;
import java.util.Map;

import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.ldap.context.LdapContext;
import org.mgnl.nicki.template.loader.JndiTemplateLoader;

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;

public class ConfigurationFactory {

	private static ConfigurationFactory instance = new ConfigurationFactory();
	private Map<String, Configuration> configurations = new HashMap<String, Configuration>();
	public Configuration getConfiguration(String baseDn) throws InvalidPrincipalException {
		if (!configurations.containsKey(baseDn)) {
			JndiTemplateLoader loader = new JndiTemplateLoader((LdapContext) AppContext.getSystemContext(), baseDn);
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
