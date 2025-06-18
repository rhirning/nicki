
package org.mgnl.nicki.db.helper;

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


import org.apache.commons.lang3.StringUtils;


/**
 * The Class ColumnsAndValues.
 */
public class ColumnsAndValues {
	
	/** The columns. */
	private String columns;
	
	/** The values. */
	private String values;
	
	/**
	 * Instantiates a new columns and values.
	 *
	 * @param columns the columns
	 * @param values the values
	 */
	public ColumnsAndValues(String columns, String values) {
		this.columns = columns;
		this.values = values;
	}

	/**
	 * Adds the.
	 *
	 * @param name the name
	 * @param value the value
	 */
	public void add(String  name, String value) {
		if (StringUtils.isNotEmpty(columns)) {
			columns += BasicDBHelper.COLUMN_SEPARATOR;
			values += BasicDBHelper.COLUMN_SEPARATOR;
		}
		columns += name;
		values += value;

	}

	/**
	 * Gets the columns.
	 *
	 * @return the columns
	 */
	public String getColumns() {
		return columns;
	}

	/**
	 * Gets the values.
	 *
	 * @return the values
	 */
	public String getValues() {
		return values;
	}

}
