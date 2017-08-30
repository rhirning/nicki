/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

public class DbcpConfiguration {

    private String dbDriverName;
    private String dbUser;
    private String dbPassword;
    private String dbURI;

    private int dbPoolMinSize;
    private int dbPoolMaxSize;

    public DbcpConfiguration(String base) throws InvalidConfigurationException {
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
