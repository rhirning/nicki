
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

import org.mgnl.nicki.db.connection.ConnectionManager;
import org.mgnl.nicki.db.connection.InvalidConfigurationException;


public class DBCPProfile implements DBProfile {
	private boolean autoCommit = false;
	private ConnectionManager connectionManager;

	public DBCPProfile(String profileConfigBase, boolean autoCommit) throws InvalidConfigurationException {
		super();
		this.autoCommit = autoCommit;
		this.connectionManager = new ConnectionManager();
		this.connectionManager.init(profileConfigBase);
	}

	public Connection getConnection() throws SQLException  {
		Connection connection = this.connectionManager.getConnection();
		connection.setAutoCommit(this.autoCommit);
		return connection;
	}

}
