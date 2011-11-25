package org.mgnl.nicki.db.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface SelectHandler {

	String getSearchStatement() throws Exception;

	void handle(ResultSet rs) throws SQLException;
	
	boolean isLoggingEnabled();

}
