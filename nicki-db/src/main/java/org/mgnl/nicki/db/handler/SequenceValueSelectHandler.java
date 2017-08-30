/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.db.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SequenceValueSelectHandler extends NonLoggingSelectHandler implements SelectHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(SequenceValueSelectHandler.class);
	
	private String sequenceName;
	private long result = -1;

	public SequenceValueSelectHandler(String sequenceName) {
		this.sequenceName = sequenceName;
	}


	public String getSearchStatement() {
		String statement = "select " + getSequenceName() + ".nextval from dual";
		LOG.debug(statement);
		return statement;
	}


	public void handle(ResultSet rs) throws SQLException {
		if (rs.next()) {
			result = rs.getLong(1);
		}
	}


	public long getResult() {
		return result;
	}


	public String getSequenceName() {
		return sequenceName;
	}
}

