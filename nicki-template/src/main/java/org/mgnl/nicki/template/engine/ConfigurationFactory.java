
package org.mgnl.nicki.template.engine;

/*-
 * #%L
 * nicki-template
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


import java.util.HashMap;
import java.util.Map;

import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.template.loader.ClassPathTemplateLoader;
import org.mgnl.nicki.template.loader.JndiTemplateLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.TemplateLoader;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;

public class ConfigurationFactory {
	private static final Logger LOG = LoggerFactory.getLogger(ConfigurationFactory.class);
	
	public enum TYPE {JNDI, CLASSPATH};

	private static ConfigurationFactory instance = new ConfigurationFactory();
	private Map<String, Configuration> configurations = new HashMap<String, Configuration>();
	public Configuration getConfiguration(TYPE type, String base) {
		String key = type.name() + "-" + base;
		if (!configurations.containsKey(key)) {
			Configuration cfg = new Configuration(Configuration.VERSION_2_3_27);
			cfg.setObjectWrapper(new BeansWrapperBuilder(Configuration.VERSION_2_3_27).build());
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
