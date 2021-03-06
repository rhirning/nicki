
package org.mgnl.nicki.db.context;

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
import java.util.HashMap;
import java.util.Map;

import org.mgnl.nicki.db.helper.BeanHelper;
import org.mgnl.nicki.db.helper.Type;

public class PrimaryKey {

	private Map<String, Object> values = new HashMap<>();
	private Map<String, Type> types = new HashMap<>();
	
	public PrimaryKey(Class<?> beanClass, String generatedColumns[], ResultSet generatedKeys) throws SQLException {
		if (beanClass != null && generatedColumns != null && generatedKeys != null) {
			String columnName = generatedColumns[0];
			Type type = BeanHelper.getTypeOfColumn(beanClass, columnName);
			if (generatedKeys.next()) {
				Object value = getValue(generatedKeys, 1, type);
				values.put(columnName, value);
				types.put(columnName, type);
			}
		}
	}

	private Object getValue(ResultSet generatedKeys, int pos, Type type) throws SQLException {
		if (type == Type.STRING || type == Type.UNKONWN) {
			return generatedKeys.getString(pos);
		} else if (type == Type.TIMESTAMP) {
			return generatedKeys.getTimestamp(pos);
		} else if (type == Type.DATE) {
			return generatedKeys.getDate(pos);
		} else if (type == Type.LONG) {
			return generatedKeys.getLong(pos);
		} else if (type == Type.INT) {
			return generatedKeys.getInt(pos);
		} else if (type == Type.FLOAT) {
			return generatedKeys.getFloat(pos);
		} else if (type == Type.BOOLEAN) {
			return generatedKeys.getBoolean(pos);
		}
		return null;
	}

	public PrimaryKey(Class<?> beanClass, String columnName, Object value) {
		Type type = BeanHelper.getTypeOfColumn(beanClass, columnName);
		values.put(columnName, value);
		types.put(columnName, type);
	}

	public PrimaryKey() {
	}

	public void add(Class<?> beanClass, String columnName, Object value) {
		Type type = BeanHelper.getTypeOfColumn(beanClass, columnName);
		values.put(columnName, value);
		types.put(columnName, type);
	}

	public Object getValue(String columnName) {
		return values.get(columnName);
	}

	@SuppressWarnings("unchecked")
	private <T> T getValue(Class<T> clazz, String columnName) {
		if (types.containsKey(columnName) && types.get(columnName).match(clazz)) {
			return (T) values.get(columnName);
		} else {
			return null;
		}
	}

	public long getLong(String columnName) {
		return getValue(long.class, columnName);
	}

	public int getInt(String columnName) {
		return getValue(int.class, columnName);
	}

	public String getString(String columnName) {
		return getValue(String.class, columnName);
	}

	public int size() {
		return values.size();
	}

}
