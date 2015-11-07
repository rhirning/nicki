package org.mgnl.nicki.db.profile;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class JndiDBProfile implements DBProfile {
	String jndiEnvironment = "java:comp/env";
	String dataSource = null;
	
	public JndiDBProfile(String dataSource) {
		super();
		this.dataSource = dataSource;
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
		return ds.getConnection();
	}
	
}
