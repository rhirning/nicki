package org.mgnl.nicki.db.profile;

import java.sql.Connection;

import org.mgnl.nicki.db.connection.ConnectionManager;


public class DBCPProfile implements DBProfile {
	private String name = null;

	public DBCPProfile(String name) {
		super();
		this.name = name;
	}

	public Connection getConnection() throws Exception {
		return ConnectionManager.getInstance().getConnection(this.name);
	}

}
