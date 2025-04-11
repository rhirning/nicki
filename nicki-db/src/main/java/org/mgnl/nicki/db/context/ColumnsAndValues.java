
package org.mgnl.nicki.db.context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mgnl.nicki.db.context.BaseDBContext.PREPARED;
import org.mgnl.nicki.db.helper.BeanHelper;
import org.mgnl.nicki.db.helper.Type;

// TODO: Auto-generated Javadoc
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

/*
 * Names are columnNames!!!
 */

/**
 * The Class ColumnsAndValues.
 */
public class ColumnsAndValues implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -712686267178848355L;
	
	/** The Constant COLUMN_SEPARATOR. */
	public final static String COLUMN_SEPARATOR = ", ";
	
	/** The Constant PREP_VALUE. */
	public final static String PREP_VALUE = "?";
	
	/** The column names. */
	private List<String> columnNames = new ArrayList<>();
	
	/** The values. */
	private Map<String, Object> values = new HashMap<>();
	
	/** The types. */
	private Map<String, Type> types = new HashMap<>();
	
	/** The bean class. */
	private Class<?> beanClass;
	
	/**
	 * Instantiates a new columns and values.
	 *
	 * @param beanClass the bean class
	 */
	public ColumnsAndValues(Class<?> beanClass) {
		super();
		this.beanClass = beanClass;
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String name : columnNames) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(name).append("(").append(types.get(name)).append(")=");
			sb.append(values.get(name));
		}
		sb.insert(0, "[");
		sb.append("]");
		return sb.toString();
	}
	
	


	/**
	 * Adds the.
	 *
	 * @param columnName the column name
	 * @param value the value
	 */
	public void add(String columnName, Object value) {
		columnNames.add(columnName);
		values.put(columnName, value);
		types.put(columnName, BeanHelper.getTypeOfColumn(beanClass, columnName));
	}
	

	/**
	 * Gets the columns.
	 *
	 * @return the columns
	 */
	public String getColumns() {
		StringBuilder sb = new StringBuilder();
		for (String columnName : columnNames) {
			if (sb.length() > 0) {
				sb.append(COLUMN_SEPARATOR);
			}
			sb.append(columnName);
		}
		return sb.toString();
	}

	/**
	 * Gets the values.
	 *
	 * @param dbContext the db context
	 * @param prepared the prepared
	 * @return the values
	 */
	public String getValues(DBContext dbContext, PREPARED prepared) {
		StringBuilder sb = new StringBuilder();
		for (String columnName : columnNames) {
			if (sb.length() > 0) {
				sb.append(COLUMN_SEPARATOR);
			}
			if (prepared == PREPARED.TRUE) {
				sb.append("?");
				
			} else {
				sb.append(getDbString(dbContext, columnName));
			}
		}
		return sb.toString();
	}

	/**
	 * Gets the prepared values.
	 *
	 * @return the prepared values
	 */
	public String getPreparedValues() {
		StringBuilder sb = new StringBuilder();
		for (@SuppressWarnings("unused") String columnName : columnNames) {
			if (sb.length() > 0) {
				sb.append(COLUMN_SEPARATOR);
			}
			sb.append(PREP_VALUE);
		}
		return sb.toString();
	}

	/**
	 * Gets the column names.
	 *
	 * @return the column names
	 */
	public List<String> getColumnNames() {
		return columnNames;
	}

	/**
	 * Size.
	 *
	 * @return the int
	 */
	public int size() {
		return columnNames.size();
	}
	
	/**
	 * Gets the type.
	 *
	 * @param columnName the column name
	 * @return the type
	 */
	public Type getType(String columnName) {
		if (columnNames.contains(columnName)) {
			return types.get(columnName);
		} else {
			return Type.UNKONWN;
		}
	}
	
	/**
	 * Gets the value.
	 *
	 * @param columnName the column name
	 * @return the value
	 */
	public Object getValue(String columnName) {
		return values.get(columnName);
	}


	/**
	 * Gets the db string.
	 *
	 * @param dbContext the db context
	 * @param columnName the column name
	 * @return the db string
	 */
	public String getDbString(DBContext dbContext, String columnName) {
		if (columnNames.contains(columnName)) {
			Type type = types.get(columnName);
			return Type.getDbString(dbContext, type, values.get(columnName));
		} else {
			return null;
		}
	}
}
