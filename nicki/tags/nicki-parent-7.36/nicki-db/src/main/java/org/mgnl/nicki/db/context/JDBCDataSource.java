package org.mgnl.nicki.db.context;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.mgnl.nicki.db.context.DBContext;
import org.mgnl.nicki.db.profile.InitProfileException;

public class JDBCDataSource implements DataSource {

	private PrintWriter logWriter;
	private int loginTimeout;
	private DBContext dbContext;

	public JDBCDataSource(DBContext dbContext) {
		super();
		this.dbContext = dbContext;
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

}
