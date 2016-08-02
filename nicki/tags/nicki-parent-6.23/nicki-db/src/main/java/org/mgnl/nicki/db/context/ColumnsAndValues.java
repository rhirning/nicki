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
