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
package org.mgnl.nicki.db.statistics;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.db.context.DBContext;

public class Variable {
	
	enum TYPE {
		STRING {
			@Override
			String toString(DBContext dbContext, String value) {
				return "'" + value + "'";
			}

			@Override
			String toString(ResultSet resultSet, String name) throws SQLException {
				return resultSet.getString(name);
			}
		},
		DATE {
			@Override
			String toString(DBContext dbContext, String value) throws ParseException {
				Date date = DataHelper.dateFromDisplayDay(value);
				return dbContext.toDate(date);
			}

			@Override
			String toString(ResultSet resultSet, String name) throws SQLException {
				Date date = resultSet.getDate(name);
				return DataHelper.getDisplayDay(date);
			}
		},
		TIMESTAMP {
			@Override
			String toString(DBContext dbContext, String value) throws ParseException {
				Date timestamp = DataHelper.milliFromString(value);
				return dbContext.toDate(timestamp);
			}

			@Override
			String toString(ResultSet resultSet, String name) throws SQLException {
				Date date = resultSet.getTimestamp(name);
				return DataHelper.getMilli(date);
			}
		},
		INTEGER {
			@Override
			String toString(DBContext dbContext, String value) throws ParseException {
				return value;
			}

			@Override
			String toString(ResultSet resultSet, String name) throws SQLException {
				return Integer.toString(resultSet.getInt(name));
			}
		};
		
		abstract String toString(DBContext dbContext, String value) throws ParseException;

		abstract String toString(ResultSet resultSet, String name) throws SQLException;
	};
	
	public String toString(DBContext dbContext, String value) throws ParseException {
		return getVariableType().toString(dbContext, value);
	}
	
	private String name;
	
	private String type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public TYPE getVariableType() {
		return TYPE.valueOf(type);
	}

	public String toString(ResultSet resultSet) throws SQLException {
		return getVariableType().toString(resultSet, name);
	}

}
