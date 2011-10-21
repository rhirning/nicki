package org.mgnl.nicki.db.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MaxIntValueSelectHandler extends NonLoggingSelectHandler implements SelectHandler {
	
	private String tableName = null;
	private String column = null;
	private int result = 1;

	public MaxIntValueSelectHandler(String tableName, String column) {
		this.tableName = tableName;
		this.column = column;
	}


	public String getSearchStatement() {
		return "select max(" + column + ") from " + tableName;
	}


	public void handle(ResultSet rs) throws SQLException {
		if (rs.next()) {
			result = rs.getInt(1)+1;
		}
	}


	public int getResult() {
		return result;
	}
}

