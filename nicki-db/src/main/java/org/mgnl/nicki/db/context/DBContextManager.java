
package org.mgnl.nicki.db.context;

/*-
 * #%L
 * nicki-db
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

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.db.connection.InvalidConfigurationException;
import org.mgnl.nicki.db.profile.DBCPProfile;
import org.mgnl.nicki.db.profile.DBProfile;
import org.mgnl.nicki.db.profile.JndiDBProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DBContextManager {
	private static final Logger LOG = LoggerFactory.getLogger(DBContextManager.class);

	private static final String PROPERTY_CONTEXTS = "nicki.db.contexts";
	private static final String PROPERTY_CONTEXT_BASE = "nicki.db.context";
	private static final String PROPERTY_CONTEXT_CLASS_NAME = "contextClassName";
	private static final String PROPERTY_CONTEXT_DATA_SOURCE = "datasource";
	private static final String PROPERTY_CONTEXT_CONNECTION_TYPE = "type";
	private static final String PROPERTY_CONTEXT_SCHEMA = "schema";
	private static final String PROPERTY_CONTEXT_AUTO_COMMIT = "autocommit";
	private static final String SEPARATOR = ",";
	
	
	private static DBContextManager instance;
	private Map<String, String> contextClassNames = new HashMap<>();
	private Map<String, String> schemas = new HashMap<>();
	private Map<String, DBProfile> profiles = new HashMap<>();

	public synchronized static DBContextManager getInstance() {
		if (instance == null) {
			instance = new DBContextManager();
			instance.init();
		}
		return instance;
	}
	
	public void init() {
		contextClassNames.clear();
		schemas.clear();
		profiles.clear();
		for (String contextName : Config.getList(PROPERTY_CONTEXTS, SEPARATOR)) {
			try {
				String contextBase = PROPERTY_CONTEXT_BASE + "." + contextName + ".";
				String contextClassName = Config.getString(contextBase
						+ PROPERTY_CONTEXT_CLASS_NAME);
				contextClassNames.put(contextName, contextClassName);
				
				String schema = Config.getString(contextBase + PROPERTY_CONTEXT_SCHEMA);
				if (StringUtils.isNotBlank(schema)) {
					schemas.put(contextName, schema);
				}					
			} catch (Exception e) {
				LOG.error("error init DBContexts", e);
			}
		}
	}

	private DBProfile createProfile(String contextName) throws InvalidConfigurationException {
		String contextBase = PROPERTY_CONTEXT_BASE + "." + contextName + ".";
		String type  = Config.getString(contextBase + PROPERTY_CONTEXT_CONNECTION_TYPE);
		boolean autoCommit  = DataHelper.booleanOf(Config.getProperty(contextBase + PROPERTY_CONTEXT_AUTO_COMMIT, "false")) ;
		
		if (StringUtils.equals("dbcp", type)) {
			String profileConfigBase = contextBase + "dbcp.";
			return new DBCPProfile(profileConfigBase, autoCommit);
		} else {
			String dataSource = Config.getString(contextBase + PROPERTY_CONTEXT_DATA_SOURCE);
			return new JndiDBProfile(dataSource, autoCommit);
		}
	}
	
	public static DBContext getContext(String name) {
		return getInstance().loadContext(name);
	}
	
	private DBContext loadContext(String name) {
		try {
			DBContext context = Classes.newInstance(contextClassNames.get(name));
			context.setName(name);

			if (schemas.containsKey(name)) {
				context.setSchema(schemas.get(name));
			}
			context.setSchema(schemas.get(name));
			context.setProfile(getProfile(name));
			return context;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | InvalidConfigurationException e) {
			LOG.error("error loading DBContext " + name, e);
		}
		return null;
	}
	
	private DBProfile getProfile(String contextName) throws InvalidConfigurationException {
		if (!this.profiles.containsKey(contextName)) {
			DBProfile profile = createProfile(contextName);
			LOG.debug("init profile " + profile);
			this.profiles.put(contextName, profile);

		}
		return this.profiles.get(contextName);
	}
	
	
}
