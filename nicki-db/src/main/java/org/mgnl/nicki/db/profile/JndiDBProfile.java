package org.mgnl.nicki.db.profile;

import java.sql.Connection;

import javax.naming.Context;
import javax.naming.InitialContext;
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
	
	public Connection getConnection() throws Exception{
		Context initContext = new InitialContext();
		Context envContext = (Context) initContext.lookup(getJndiEnvironment());
		DataSource ds = (DataSource) envContext.lookup(getDataSource());
		return ds.getConnection();
	}
	
}
