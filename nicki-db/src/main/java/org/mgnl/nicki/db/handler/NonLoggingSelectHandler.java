
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
 * The Class NonLoggingSelectHandler.
 */
public abstract class NonLoggingSelectHandler implements SelectHandler {

	/**
	 * Gets the search statement.
	 *
	 * @return the search statement
	 */
	public abstract String getSearchStatement();

	/**
	 * Handle.
	 *
	 * @param rs the rs
	 * @throws SQLException the SQL exception
	 */
	public abstract void handle(ResultSet rs) throws SQLException;
	
	/**
	 * Checks if is logging enabled.
	 *
	 * @return true, if is logging enabled
	 */
	public boolean isLoggingEnabled() {
		return false;
	}

}
