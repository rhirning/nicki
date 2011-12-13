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

public abstract class NonLoggingSelectHandler implements SelectHandler {

	public abstract String getSearchStatement() throws Exception;

	public abstract void handle(ResultSet rs) throws SQLException;
	
	public boolean isLoggingEnabled() {
		return false;
	}

}
