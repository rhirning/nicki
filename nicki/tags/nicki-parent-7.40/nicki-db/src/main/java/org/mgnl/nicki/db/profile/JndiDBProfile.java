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
