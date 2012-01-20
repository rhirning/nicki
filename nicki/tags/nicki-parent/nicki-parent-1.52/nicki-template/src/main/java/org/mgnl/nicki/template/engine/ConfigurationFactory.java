/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
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
