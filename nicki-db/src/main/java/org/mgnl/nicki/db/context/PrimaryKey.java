
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

import org.mgnl.nicki.db.helper.Type;

public class PrimaryKey {

	private Map<String, Object> values = new HashMap<>();
	private Map<String, Type> types = new HashMap<>();
	
	public PrimaryKey(Class<?> beanClazz, String generatedColumns[], ResultSet generatedKeys) throws SQLException {
		if (beanClazz != null && generatedColumns != null && generatedKeys != null) {
			int i = 0;
			while(generatedKeys.next()) {
				String name = generatedColumns[i];
				Type type = Type.getBeanAttributeType(beanClazz, name);
				Object value = getValue(generatedKeys, name, type);
				values.put(name, value);
				types.put(name, type);
				i++;
			}
		}
	}

	private Object getValue(ResultSet generatedKeys, String name, Type type) throws SQLException {
		if (type == Type.STRING || type == Type.UNKONWN) {
			return generatedKeys.getString(name);
		} else if (type == Type.TIMESTAMP) {
			return generatedKeys.getTimestamp(name);
		} else if (type == Type.DATE) {
			return generatedKeys.getDate(name);
		} else if (type == Type.LONG) {
			return generatedKeys.getLong(name);
		} else if (type == Type.INT) {
			return generatedKeys.getInt(name);
		}
		return null;
	}

	public PrimaryKey(Class<?> beanClazz, String name, Object value) {
		Type type = Type.getBeanAttributeType(beanClazz, name);
		values.put(name, value);
		types.put(name, type);
	}

	public PrimaryKey() {
	}

	public void add(Class<?> beanClazz, String name, Object value) {
		Type type = Type.getBeanAttributeType(beanClazz, name);
		values.put(name, value);
		types.put(name, type);
	}

	public Object getValue(String name) {
		return values.get(name);
	}

	@SuppressWarnings("unchecked")
	private <T> T getValue(Class<T> clazz, String name) {
		if (types.containsKey(name) && types.get(name).match(clazz)) {
			return (T) values.get(name);
		} else {
			return null;
		}
	}

	public long getLong(String name) {
		return getValue(long.class, name);
	}

	public int getInt(String name) {
		return getValue(int.class, name);
	}

	public String getString(String name) {
		return getValue(String.class, name);
	}

}
