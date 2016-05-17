package org.mgnl.nicki.db.context;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PrimaryKey {

	private Object value;
	
	public PrimaryKey(Class<?> clazz, ResultSet generatedKeys) throws SQLException {
		if (clazz != null && generatedKeys != null && generatedKeys.next()) {
			if (Long.class.isAssignableFrom(clazz)) {
				this.value = generatedKeys.getLong(1);
			} else if (String.class.isAssignableFrom(clazz)) {
				this.value = generatedKeys.getString(1);
			}
		}
	}

	public PrimaryKey(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
