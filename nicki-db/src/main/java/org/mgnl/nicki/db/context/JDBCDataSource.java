
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


/**
 * The Class JDBCDataSource.
 */
public class JDBCDataSource implements DataSource {

	/** The log writer. */
	private PrintWriter logWriter;
	
	/** The login timeout. */
	private int loginTimeout;
	
	/** The context. */
	private String context;

	/**
	 * Instantiates a new JDBC data source.
	 *
	 * @param context the context
	 */
	public JDBCDataSource(String context) {
		super();
		this.context = context;
	}

	/**
	 * Gets the log writer.
	 *
	 * @return the log writer
	 * @throws SQLException the SQL exception
	 */
	@Override
	public PrintWriter getLogWriter() throws SQLException {
        if (logWriter == null) {
            logWriter = new PrintWriter(System.out);
        }        
        return logWriter;
	}

	/**
	 * Sets the log writer.
	 *
	 * @param out the new log writer
	 * @throws SQLException the SQL exception
	 */
	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		this.logWriter = out;
	}

	/**
	 * Sets the login timeout.
	 *
	 * @param seconds the new login timeout
	 * @throws SQLException the SQL exception
	 */
	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
        this.loginTimeout = seconds;
	}

	/**
	 * Gets the login timeout.
	 *
	 * @return the login timeout
	 * @throws SQLException the SQL exception
	 */
	@Override
	public int getLoginTimeout() throws SQLException {
		return loginTimeout;
	}

	/**
	 * Gets the parent logger.
	 *
	 * @return the parent logger
	 * @throws SQLFeatureNotSupportedException the SQL feature not supported exception
	 */
	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException();
	}

	/**
	 * Unwrap.
	 *
	 * @param <T> the generic type
	 * @param iface the iface
	 * @return the t
	 * @throws SQLException the SQL exception
	 */
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException("JDBCDataSource is not a wrapper.");
	}

	/**
	 * Checks if is wrapper for.
	 *
	 * @param iface the iface
	 * @return true, if is wrapper for
	 * @throws SQLException the SQL exception
	 */
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 * @throws SQLException the SQL exception
	 */
	@Override
	public Connection getConnection() throws SQLException {
		return getConnection(null, null);
	}

	/**
	 * Gets the connection.
	 *
	 * @param username the username
	 * @param password the password
	 * @return the connection
	 * @throws SQLException the SQL exception
	 */
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

	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	public String getContext() {
		return context;
	}

	/**
	 * Sets the context.
	 *
	 * @param context the new context
	 */
	public void setContext(String context) {
		this.context = context;
	}

}
