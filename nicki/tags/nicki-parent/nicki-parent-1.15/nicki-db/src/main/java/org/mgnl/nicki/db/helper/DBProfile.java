package org.mgnl.nicki.db.helper;

import java.sql.Connection;

public interface DBProfile {
	public Connection getConnection()  throws Exception;
}
