
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


import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.mgnl.nicki.db.profile.InitProfileException;

public class JDBCDataSource implements DataSource {

	private PrintWriter logWriter;
	private int loginTimeout;
	private String context;

	public JDBCDataSource(String context) {
		super();
		this.context = context;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
        if (logWriter == null) {
            logWriter = new PrintWriter(System.out);
        }        
        return logWriter;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		this.logWriter = out;
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
        this.loginTimeout = seconds;
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return loginTimeout;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException("JDBCDataSource is not a wrapper.");
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return getConnection(null, null);
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		DBContext dbContext = DBContextManager.getContext(this.context);
		if (dbContext != null) {
			try {
				return dbContext.beginTransaction();
			} catch (InitProfileException e) {
				throw new SQLException("Could not get connection", e);
			}
		} else {
			throw new SQLException("invalid DBContext configuration");
		}
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

}
