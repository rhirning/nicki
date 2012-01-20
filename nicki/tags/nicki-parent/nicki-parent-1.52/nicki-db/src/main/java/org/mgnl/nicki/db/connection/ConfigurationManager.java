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
package org.mgnl.nicki.db.connection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration file. Format:
# connections
nicki.db.connections = a,b
# connection a
nicki.db.connection.a.driverClassName = com.ibm.db2.jcc.DB2Driver
nicki.db.connection.a.username = Karl
nicki.db.connection.a.password = !BASE64EncodedPassword
nicki.db.connection.a.url = jdbc:db2://172.17.2.2:50000/MyDB
nicki.db.connection.a.minActive = 10
nicki.db.connection.a.maxActive = 100

# connection b
 
nicki.db.connection.b.driverClassName = com.ibm.db2.jcc.DB2Driver
nicki.db.connection.b.username = Karl
nicki.db.connection.b.password = !BASE64EncodedPassword
nicki.db.connection.b.url = jdbc:db2://172.17.2.2:50000/YourDB
nicki.db.connection.b.minActive = 10
nicki.db.connection.b.maxActive = 100
  
 * @author rhi
 *
 */
public class ConfigurationManager {

	private static final String PROPERTY_CONNECTIONS = "nicki.db.connections";
	private static final String PROPERTY_CONNECTION_BASE = "nicki.db.connection";
	private static final String SEPARATOR = ",";
	
	private static ConfigurationManager instance = new ConfigurationManager();
	private Map<String, Configuration> configurations
		= new HashMap<String, Configuration>();

    private static final Logger LOG = LoggerFactory.getLogger( ConfigurationManager.class );

    public ConfigurationManager() {
	}

	public void configChanged() {
		this.configurations.clear();
		String connections = Config.getProperty(PROPERTY_CONNECTIONS);
		if (StringUtils.isNotEmpty(connections)) {
			for (Iterator<String> iterator = DataHelper.getList(connections, SEPARATOR).iterator(); iterator.hasNext();) {
				addConfiguration(iterator.next());
				
			}
		}
    }

	private void addConfiguration(String name) {
        try {
	    	Configuration configuration = new Configuration(PROPERTY_CONNECTION_BASE, name);
	    	this.configurations.put(name, configuration);
        }   catch ( Exception ex ) {
            LOG.error( "Invalid configuration: " + name);
        }

    }
    public static ConfigurationManager getInstance() {
   		return instance;
    }

	public Map<String, Configuration> getConfigurations() {
		return configurations;
	}

	public Configuration getConfiguration(String id) {
		return this.configurations.get(id);
	}
}
