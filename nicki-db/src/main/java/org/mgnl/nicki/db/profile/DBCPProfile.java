package org.mgnl.nicki.db.profile;

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
