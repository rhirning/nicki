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

import org.apache.commons.lang.StringUtils;

public class CountSelectHandler extends NonLoggingSelectHandler implements SelectHandler {

	private String tableName;
	private String whereClause;
	private int result;

	public CountSelectHandler(String tableName, String whereClause) {
		this.tableName = tableName;
		this.whereClause = whereClause;
	}


	public String getSearchStatement() {
		StringBuilder sb = new StringBuilder();
		sb.append("select count(*) from ").append(tableName);
		if (StringUtils.isNotBlank(whereClause)) {
			sb.append(" where ").append(whereClause);
		}
		return sb.toString();
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

