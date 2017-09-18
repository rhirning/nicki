
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

public class PrimaryKey {

	private String value;
	
	public PrimaryKey(Class<?> clazz, ResultSet generatedKeys) throws SQLException {
		if (clazz != null && generatedKeys != null && generatedKeys.next()) {
			this.value = generatedKeys.getString(1);
		}
	}

	public PrimaryKey(Object rawValue) {
		if (rawValue instanceof String) {
			value = (String) rawValue;
		} else if (rawValue instanceof Long) {
			value = Long.toString((long) rawValue);
		} else if (rawValue instanceof Integer) {
			value = Integer.toString((int) rawValue);
		}
	}

	public String getValue() {
		return value;
	}

	public long getLong() {
		return Long.parseLong(value);
	}

	public int getInt() {
		return Integer.parseInt(value);
	}

	public void setValue(String value) {
		this.value = value;
	}

}
