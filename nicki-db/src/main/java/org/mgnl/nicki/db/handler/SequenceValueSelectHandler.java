
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


import lombok.extern.slf4j.Slf4j;


/**
 * The Class SequenceValueSelectHandler.
 */
@Slf4j
public class SequenceValueSelectHandler extends NonLoggingSelectHandler implements SelectHandler {
	
	/** The sequence name. */
	private String sequenceName;
	
	/** The result. */
	private long result = -1;

	/**
	 * Instantiates a new sequence value select handler.
	 *
	 * @param sequenceName the sequence name
	 */
	public SequenceValueSelectHandler(String sequenceName) {
		this.sequenceName = sequenceName;
	}


	/**
	 * Gets the search statement.
	 *
	 * @return the search statement
	 */
	public String getSearchStatement() {
		String statement = "select " + getSequenceName() + ".nextval from dual";
		log.debug(statement);
		return statement;
	}


	/**
	 * Handle.
	 *
	 * @param rs the rs
	 * @throws SQLException the SQL exception
	 */
	public void handle(ResultSet rs) throws SQLException {
		if (rs.next()) {
			result = rs.getLong(1);
		}
	}


	/**
	 * Gets the result.
	 *
	 * @return the result
	 */
	public long getResult() {
		return result;
	}


	/**
	 * Gets the sequence name.
	 *
	 * @return the sequence name
	 */
	public String getSequenceName() {
		return sequenceName;
	}
}

