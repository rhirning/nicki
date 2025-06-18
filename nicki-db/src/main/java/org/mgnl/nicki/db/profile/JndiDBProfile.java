
package org.mgnl.nicki.db.profile;

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


import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


/**
 * The Class JndiDBProfile.
 */
public class JndiDBProfile implements DBProfile {
	
	/** The jndi environment. */
	String jndiEnvironment = "java:comp/env";
	
	/** The data source. */
	String dataSource;
	
	/** The auto commit. */
	private boolean autoCommit;
	
	/**
	 * Instantiates a new jndi DB profile.
	 *
	 * @param dataSource the data source
	 * @param autoCommit the auto commit
	 */
	public JndiDBProfile(String dataSource, boolean autoCommit) {
		super();
		this.dataSource = dataSource;
		this.autoCommit = autoCommit;
	}

	/**
	 * Gets the jndi environment.
	 *
	 * @return the jndi environment
	 */
	public String getJndiEnvironment() {
		return jndiEnvironment;
	}

	/**
	 * Gets the data source.
	 *
	 * @return the data source
	 */
	public String getDataSource() {
		return dataSource;
	}
	
	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 */
	public Connection getConnection() throws SQLException, InitProfileException {
		DataSource ds;
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup(getJndiEnvironment());
			ds = (DataSource) envContext.lookup(getDataSource());
		} catch (NamingException e) {
			throw new InitProfileException(e);
		}
		Connection connection = ds.getConnection();
		connection.setAutoCommit(this.autoCommit);
		return connection;
	}
	
}
