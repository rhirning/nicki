package org.mgnl.nicki.db.statistics;

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
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.db.context.DBContext;

// TODO: Auto-generated Javadoc
/**
 * The Class Variable.
 */
public class Variable {
	
	/**
	 * The Enum TYPE.
	 */
	enum TYPE {
		
		/** The string. */
		STRING {
			@Override
			String toString(DBContext dbContext, String value) {
				return "'" + value + "'";
			}

			@Override
			String toString(ResultSet resultSet, String name) throws SQLException {
				return StringUtils.stripToEmpty(resultSet.getString(name));
			}
		},
		
		/** The list. */
		LIST {
			@Override
			String toString(DBContext dbContext, String value) {
				if (StringUtils.isBlank(value)) {
					return "'" + value + "'";
				}
				
				String[] list = StringUtils.split(StringUtils.trimToEmpty(value), ",");
				StringBuilder sb = new StringBuilder();
				for (String entry : list) {
					if (sb.length() > 0) {
						sb.append(",");
					}
					sb.append("'").append(entry).append("'");
				}
				return sb.toString();
			}

			@Override
			String toString(ResultSet resultSet, String name) throws SQLException {
				return StringUtils.stripToEmpty(resultSet.getString(name));
			}
		},
		
		/** The date. */
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
		
		/** The timestamp. */
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
		
		/** The integer. */
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
		
		/**
		 * To string.
		 *
		 * @param dbContext the db context
		 * @param value the value
		 * @return the string
		 * @throws ParseException the parse exception
		 */
		abstract String toString(DBContext dbContext, String value) throws ParseException;

		/**
		 * To string.
		 *
		 * @param resultSet the result set
		 * @param name the name
		 * @return the string
		 * @throws SQLException the SQL exception
		 */
		abstract String toString(ResultSet resultSet, String name) throws SQLException;
	};
	
	/**
	 * To string.
	 *
	 * @param dbContext the db context
	 * @param value the value
	 * @return the string
	 * @throws ParseException the parse exception
	 */
	public String toString(DBContext dbContext, String value) throws ParseException {
		return getVariableType().toString(dbContext, value);
	}
	
	/** The name. */
	private String name;
	
	/** The type. */
	private String type;

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Gets the variable type.
	 *
	 * @return the variable type
	 */
	public TYPE getVariableType() {
		return TYPE.valueOf(type);
	}

	/**
	 * To string.
	 *
	 * @param resultSet the result set
	 * @return the string
	 * @throws SQLException the SQL exception
	 */
	public String toString(ResultSet resultSet) throws SQLException {
		return getVariableType().toString(resultSet, name);
	}

}
