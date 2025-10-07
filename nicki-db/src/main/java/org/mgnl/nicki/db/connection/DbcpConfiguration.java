
package org.mgnl.nicki.db.connection;

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

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;

/**
 * The Class DbcpConfiguration.
 */
public class DbcpConfiguration {

	/** The db driver name. */
	private String dbDriverName;

	/** The db user. */
	private String dbUser;

	/** The db password. */
	private String dbPassword;

	/** The db URI. */
	private String dbURI;

	/** The db pool min idle size. */
	private int dbPoolMinIdleSize;

	/** The db pool max idle size. */
	private int dbPoolMaxIdleSize;

	/**
	 * Instantiates a new dbcp configuration.
	 *
	 * @param base the base
	 * @throws InvalidConfigurationException the invalid configuration exception
	 */
	public DbcpConfiguration(String base) throws InvalidConfigurationException {
		dbDriverName = Config.getString(base + "driverClassName");
		dbUser = Config.getString(base + "username");
		dbPassword = DataHelper.getPassword(Config.getString(base + "password"));
		dbURI = Config.getString(base + "url");
		dbPoolMinIdleSize = Config.getInteger(base + "minIdle", 1);
		dbPoolMaxIdleSize = Config.getInteger(base + "maxIdle", 5);
		if (!isValid()) {
			throw new InvalidConfigurationException();
		}
	}

	/**
	 * Checks if is valid.
	 *
	 * @return true, if is valid
	 */
	private boolean isValid() {
		if (StringUtils.isEmpty(dbDriverName)) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the db driver name.
	 *
	 * @return the db driver name
	 */
	public String getDbDriverName() {
		return dbDriverName;
	}

	/**
	 * Gets the db user.
	 *
	 * @return the db user
	 */
	public String getDbUser() {
		return dbUser;
	}

	/**
	 * Gets the db password.
	 *
	 * @return the db password
	 */
	public String getDbPassword() {
		return dbPassword;
	}

	/**
	 * Gets the db URI.
	 *
	 * @return the db URI
	 */
	public String getDbURI() {
		return dbURI;
	}

	/**
	 * Gets the db pool min idle size.
	 *
	 * @return the db pool min idle size
	 */
	public int getDbPoolMinIdleSize() {
		return dbPoolMinIdleSize;
	}

	/**
	 * Gets the db pool max idle size.
	 *
	 * @return the db pool max idle size
	 */
	public int getDbPoolMaxIdleSize() {
		return dbPoolMaxIdleSize;
	}

}
