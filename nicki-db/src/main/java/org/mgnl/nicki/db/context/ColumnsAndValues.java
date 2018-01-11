
package org.mgnl.nicki.db.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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




public class ColumnsAndValues extends HashMap<String, String> {
	private static final long serialVersionUID = -712686267178848355L;
	public final static String COLUMN_SEPARATOR = ", ";
	public final static String PREP_VALUE = "?";
	private List<String> names = new ArrayList<>();
	
	public String add(String  name, String value) {
		names.add(name);
		return super.put(name, value);

	}
	

	public String getColumns() {
		StringBuilder sb = new StringBuilder();
		for (String name : names) {
			if (sb.length() > 0) {
				sb.append(COLUMN_SEPARATOR);
			}
			sb.append(name);
		}
		return sb.toString();
	}

	public String getValues() {
		StringBuilder sb = new StringBuilder();
		for (String name : names) {
			if (sb.length() > 0) {
				sb.append(COLUMN_SEPARATOR);
			}
			sb.append(get(name));
		}
		return sb.toString();
	}

	public String getPreparedValues() {
		StringBuilder sb = new StringBuilder();
		for (@SuppressWarnings("unused") String name : names) {
			if (sb.length() > 0) {
				sb.append(COLUMN_SEPARATOR);
			}
			sb.append(PREP_VALUE);
		}
		return sb.toString();
	}

	public List<String> getNames() {
		return names;
	}

	public int size() {
		return names.size();
	}


	@Override
	public String put(String key, String value) {
		// TODO Auto-generated method stub
		return add(key, value);
	}
}
