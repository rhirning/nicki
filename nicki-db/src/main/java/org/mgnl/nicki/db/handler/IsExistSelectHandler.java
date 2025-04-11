
package org.mgnl.nicki.db.handler;

/*-
 * #%L
 * nicki-db
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.sql.ResultSet;
import java.sql.SQLException;

// TODO: Auto-generated Javadoc
/**
 * The Class IsExistSelectHandler.
 */
public class IsExistSelectHandler extends NonLoggingSelectHandler implements SelectHandler {
	
	/** The table name. */
	private String tableName;
	
	/** The where clause. */
	private String whereClause;
	
	/** The exist. */
	private boolean exist = false;

	/**
	 * Instantiates a new checks if is exist select handler.
	 *
	 * @param tableName the table name
	 * @param whereClause the where clause
	 */
	public IsExistSelectHandler(String tableName, String whereClause) {
		super();
		this.tableName = tableName;
		this.whereClause = whereClause;
	}


	/**
	 * Gets the search statement.
	 *
	 * @return the search statement
	 */
	public String getSearchStatement() {
		return "select count(*) as anzahl from " + tableName
		+ " where " + whereClause;
	}


	/**
	 * Handle.
	 *
	 * @param rs the rs
	 * @throws SQLException the SQL exception
	 */
	public void handle(ResultSet rs) throws SQLException {
		if (rs.next() &&  rs.getInt("anzahl") > 0) {
			exist = true;
		}
	}

	/**
	 * Checks if is exist.
	 *
	 * @return true, if is exist
	 */
	public boolean isExist() {
		return exist;
	}
}

