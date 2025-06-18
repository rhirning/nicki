
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


/**
 * The Class MaxIntValueSelectHandler.
 */
public class MaxIntValueSelectHandler extends NonLoggingSelectHandler implements SelectHandler {
	
	/** The table name. */
	private String tableName;
	
	/** The column. */
	private String column;
	
	/** The result. */
	private int result = 1;

	/**
	 * Instantiates a new max int value select handler.
	 *
	 * @param tableName the table name
	 * @param column the column
	 */
	public MaxIntValueSelectHandler(String tableName, String column) {
		this.tableName = tableName;
		this.column = column;
	}


	/**
	 * Gets the search statement.
	 *
	 * @return the search statement
	 */
	public String getSearchStatement() {
		return "select max(" + column + ") from " + tableName;
	}


	/**
	 * Handle.
	 *
	 * @param rs the rs
	 * @throws SQLException the SQL exception
	 */
	public void handle(ResultSet rs) throws SQLException {
		if (rs.next()) {
			result = rs.getInt(1)+1;
		}
	}


	/**
	 * Gets the result.
	 *
	 * @return the result
	 */
	public int getResult() {
		return result;
	}
}

