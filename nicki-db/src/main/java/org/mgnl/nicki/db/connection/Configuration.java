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

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;

public class Configuration {

    private String dbDriverName = null;
    private String dbUser = null;
    private String dbPassword = null;
    private String dbURI = null;

    private int dbPoolMinSize = 0;
    private int dbPoolMaxSize = 0;

    public Configuration(String propertyBase, String name) throws InvalidConfigurationException {
    	String base = propertyBase + "." + name + ".";
        dbDriverName = Config.getProperty(base + "driverClassName");
        dbUser = Config.getProperty(base + "username");
        dbPassword = DataHelper.getPassword(Config.getProperty(base + "password"));
        dbURI = Config.getProperty(base + "url");
        dbPoolMinSize = 
        	Integer.parseInt(Config.getProperty(base + "minActive", "1"));
        dbPoolMaxSize = 
        	Integer.parseInt(Config.getProperty(base + "maxActive", "5"));
        if (!isValid()) {
        	throw new InvalidConfigurationException();
        }
    }


    private boolean isValid() {
    	if (StringUtils.isEmpty(dbDriverName)) {
    		return false;
    	}
		return true;
	}


	public String getDbDriverName() {
        return dbDriverName;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDbURI() {
        return dbURI;
    }

    public int getDbPoolMinSize() {
        return dbPoolMinSize;
    }

    public int getDbPoolMaxSize() {
        return dbPoolMaxSize;
    }

}
