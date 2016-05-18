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

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;

public class DbcpConfiguration {

    private String dbDriverName = null;
    private String dbUser = null;
    private String dbPassword = null;
    private String dbURI = null;

    private int dbPoolMinSize = 0;
    private int dbPoolMaxSize = 0;

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