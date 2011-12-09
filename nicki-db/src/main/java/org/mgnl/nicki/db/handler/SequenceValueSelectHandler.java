/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.db.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SequenceValueSelectHandler extends NonLoggingSelectHandler implements SelectHandler {
	
	private String sequenceName = null;
	private int result = -1;

	public SequenceValueSelectHandler(String sequenceName) {
		this.sequenceName = sequenceName;
	}


	public String getSearchStatement() {
		return "select " + sequenceName + ".nextval from dual";
	}


	public void handle(ResultSet rs) throws SQLException {
		if (rs.next()) {
			result = rs.getInt(1);
		}
	}


	public int getResult() {
		return result;
	}
}

