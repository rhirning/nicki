/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
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
 */
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
