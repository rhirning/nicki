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
package org.mgnl.nicki.db.connection;

import java.util.HashMap;
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
			for (String configuration : DataHelper.getList(connections, SEPARATOR)) {
				addConfiguration(configuration);
				
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
