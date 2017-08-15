package org.mgnl.nicki.db.context;

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
