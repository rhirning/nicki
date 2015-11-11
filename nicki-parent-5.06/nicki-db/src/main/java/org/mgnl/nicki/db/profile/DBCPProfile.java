package org.mgnl.nicki.db.profile;

import java.sql.Connection;
import java.sql.SQLException;

import org.mgnl.nicki.db.connection.ConnectionManager;


public class DBCPProfile implements DBProfile {
	private String name = null;
	private boolean autoCommit = false;

	public DBCPProfile(String name, boolean autoCommit) {
		super();
		this.name = name;
		this.autoCommit = autoCommit;
	}

	public Connection getConnection() throws SQLException  {
		Connection connection = ConnectionManager.getInstance().getConnection(this.name);
		connection.setAutoCommit(this.autoCommit);
		return connection;
	}

}
