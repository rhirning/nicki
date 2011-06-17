package org.mgnl.nicki.db.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IsExistSelectHandler extends NonLoggingSelectHandler implements SelectHandler {
	
	private String tableName = null;
	private String whereClause = null;
	private boolean exist = false;

	public IsExistSelectHandler(String tableName, String whereClause) {
		super();
		this.tableName = tableName;
		this.whereClause = whereClause;
	}


	public String getSearchStatement() {
		return "select count(*) as anzahl from " + tableName
		+ " where " + whereClause;
	}


	public void handle(ResultSet rs) throws SQLException {
		if (rs.next()) {
			if (rs.getInt("anzahl") > 0) {
				exist = true;
			}
		}
	}

	public boolean isExist() {
		return exist;
	}
}

