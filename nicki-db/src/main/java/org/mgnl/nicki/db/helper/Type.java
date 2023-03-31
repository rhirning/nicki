package org.mgnl.nicki.db.helper;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

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


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mgnl.nicki.db.context.DBContext;


public enum Type {
	STRING(String.class) {		
		public void fillPreparedStatement(PreparedStatement pstmt, int pos, Object rawValue) throws SQLException {
			if (rawValue != null) {
				pstmt.setString(pos, (String) rawValue);
			} else {
				pstmt.setNull(pos, Types.VARCHAR);
			}
		}
	},
	TIMESTAMP(Date.class) {		
		public void fillPreparedStatement(PreparedStatement pstmt, int pos, Object rawValue) throws SQLException {
			if (rawValue != null) {
				Date dateValue = (Date) rawValue;
				pstmt.setTimestamp(pos, new Timestamp(dateValue.getTime()));
			} else {
				pstmt.setNull(pos, Types.TIMESTAMP);
			}
		}
	},
	DATE(Date.class) {		
		public void fillPreparedStatement(PreparedStatement pstmt, int pos, Object rawValue) throws SQLException {
			if (rawValue != null) {
				Date dateValue = (Date) rawValue;
				pstmt.setDate(pos, new java.sql.Date(dateValue.getTime()));
			} else {
				pstmt.setNull(pos, Types.DATE);
			}
		}
	},
	TIME(Date.class) {		
		public void fillPreparedStatement(PreparedStatement pstmt, int pos, Object rawValue) throws SQLException {
			if (rawValue != null) {
				Date dateValue = (Date) rawValue;
				pstmt.setTime(pos, new Time(dateValue.getTime()));
			} else {
				pstmt.setNull(pos, Types.TIME);
			}
		}
	},
	LONG(Long.class, long.class) {		
		public void fillPreparedStatement(PreparedStatement pstmt, int pos, Object rawValue) throws SQLException {
			if (rawValue != null) {
				pstmt.setLong(pos, (Long) rawValue);
			} else {
				pstmt.setNull(pos, Types.BIGINT);
			}
		}
	},
	INT(Integer.class, int.class) {		
		public void fillPreparedStatement(PreparedStatement pstmt, int pos, Object rawValue) throws SQLException {
			if (rawValue != null) {
				pstmt.setInt(pos, (Integer) rawValue);
			} else {
				pstmt.setNull(pos, Types.INTEGER);
			}
		}
	},
	FLOAT(Float.class, float.class) {		
		public void fillPreparedStatement(PreparedStatement pstmt, int pos, Object rawValue) throws SQLException {
			if (rawValue != null) {
				pstmt.setFloat(pos, (Float) rawValue);
			} else {
				pstmt.setNull(pos, Types.FLOAT);
			}
		}
	},
	BOOLEAN(Boolean.class, boolean.class) {		
		public void fillPreparedStatement(PreparedStatement pstmt, int pos, Object rawValue) throws SQLException {
			if (rawValue != null) {
				Boolean value = (Boolean) rawValue;
				pstmt.setInt(pos, value? 1: 0);
			} else {
				pstmt.setNull(pos, Types.INTEGER);
			}
		}
	},
	BLOB(Byte[].class, byte[].class) {		
		public void fillPreparedStatement(PreparedStatement pstmt, int pos, Object rawValue) throws SQLException {
			if (rawValue != null) {
				byte[] value = (byte[]) rawValue;
				pstmt.setBlob(pos, new ByteArrayInputStream(value));
			} else {
				pstmt.setNull(pos, Types.BLOB);
			}
		}
	},
	UNKONWN() {
		@Override
		public void fillPreparedStatement(PreparedStatement pstmt, int pos, Object rawValue) throws SQLException {
		}
	};
		
	private List<Class<?>> classes = new ArrayList<>();
	
	private Type(Class<?> ...classes){
		if (classes != null) {
			for (Class<?> clazz : classes) {
				this.classes.add(clazz);
			}
		}
		
	}
	
	public Class<?> getTypeClass() {
		if (!classes.isEmpty()) {
			return classes.get(0);
		} else {
			return null;
		}
	}
	
	public abstract void fillPreparedStatement(PreparedStatement pstmt, int pos, Object rawValue) throws SQLException;

	public boolean match(Class<?> clazz) {
		for (Class<?> c : this.classes) {
			if (c.isAssignableFrom(clazz)) {
				return true;
			}
		}
		return false;
	}
	
	public Type getType(Class<?> clazz) {
		for (Type type : values()) {
			if (match(clazz)) {
				return type;
			}
		}
		return UNKONWN;
	}
	
	public static String getDbString(DBContext dbContext, Type type, Object value) {
		if (type == STRING) {
			return dbContext.getStringAsDbString((String) value);
		} else if (type == DATE) {
			return dbContext.getDateAsDbString((Date) value);
		} else if (type == TIME) {
			return dbContext.getTimeAsDbString((Date) value);
		} else if (type == Type.TIMESTAMP) {
			return dbContext.getTimestampAsDbString((Date) value);
		} else if (type == Type.LONG) {
			return dbContext.getLongAsDbString((Long) value);
		} else if (type == Type.INT) {
			return dbContext.getIntAsDbString( (Integer) value);
		} else if (type == Type.FLOAT) {
			return dbContext.getFloatAsDbString( (Float) value);
		} else if (type == Type.BOOLEAN) {
			return dbContext.getBooleanAsDbString( (Boolean) value);
		} else {
			return null;
		}
	}


}
