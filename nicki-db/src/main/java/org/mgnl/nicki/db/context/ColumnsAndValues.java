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
package org.mgnl.nicki.db.context;

import org.apache.commons.lang.StringUtils;

public class ColumnsAndValues {
	public final static String COLUMN_SEPARATOR = ", ";
	private String columns;
	private String values;
	
	public ColumnsAndValues(String columns, String values) {
		this.columns = columns;
		this.values = values;
	}

	public void add(String  name, String value) {
		if (StringUtils.isNotEmpty(columns)) {
			columns += COLUMN_SEPARATOR;
			values += COLUMN_SEPARATOR;
		}
		columns += name;
		values += value;

	}

	public String getColumns() {
		return columns;
	}

	public String getValues() {
		return values;
	}

}
