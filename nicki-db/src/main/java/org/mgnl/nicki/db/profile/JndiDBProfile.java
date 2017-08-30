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
package org.mgnl.nicki.db.profile;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class JndiDBProfile implements DBProfile {
	String jndiEnvironment = "java:comp/env";
	String dataSource;
	private boolean autoCommit;
	
	public JndiDBProfile(String dataSource, boolean autoCommit) {
		super();
		this.dataSource = dataSource;
		this.autoCommit = autoCommit;
	}

	public String getJndiEnvironment() {
		return jndiEnvironment;
	}

	public String getDataSource() {
		return dataSource;
	}
	
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
