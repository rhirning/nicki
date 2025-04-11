
package org.mgnl.nicki.db.data;

import java.lang.reflect.Field;
import java.util.Date;

import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.db.annotation.Attribute;

import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
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

/**
 * The Enum DataType.
 */
@Slf4j
public enum DataType {
	
	/** The default. */
	DEFAULT,
	
	/** The long. */
	LONG {
		public Object getValue(String data) {
			try {
				return Long.parseLong(data);
			} catch (Exception e) {
				return 0;
			}
		}
	},
	
	/** The int. */
	INT {
		public Object getValue(String data) {
			try {
				return Integer.parseInt(data);
			} catch (Exception e) {
				return 0;
			}
		}
	},
	
	/** The float. */
	FLOAT {
		public Object getValue(String data) {
			try {
				return Float.parseFloat(data);
			} catch (Exception e) {
				return 0;
			}
		}
	},
	
	/** The boolean. */
	BOOLEAN {
		public Object getValue(String data) {
			return DataHelper.booleanOf(data);
		}
	},
	
	/** The date. */
	DATE {
		public Object getValue(String data) {
			try {
				return DataHelper.dateFromDisplayDay(data);
			} catch (Exception e) {
				return null;
			}
		}
	},
	
	/** The timestamp. */
	TIMESTAMP {
		public Object getValue(String data) {
			try {
				return DataHelper.milliFromString(data);
			} catch (Exception e) {
				return null;
			}
		}
	},
	
	/** The time. */
	TIME {
		public Object getValue(String data) {
			try {
				return DataHelper.milliFromString(data);
			} catch (Exception e) {
				return null;
			}
		}
	},
	
	/** The clob. */
	CLOB,
	
	/** The blob. */
	BLOB;

	/**
	 * Gets the string.
	 *
	 * @param value the value
	 * @return the string
	 */
	public String getString(String value) {
		return value;
	}
	
	/**
	 * Gets the string.
	 *
	 * @param value the value
	 * @return the string
	 */
	public String getString(Date value) {
		return DataHelper.getDisplayDay(value);
	}
	
	/**
	 * Gets the string.
	 *
	 * @param value the value
	 * @return the string
	 */
	public String getString(long value) {
		return Long.toString(value);
	}
	
	/**
	 * Gets the string.
	 *
	 * @param value the value
	 * @return the string
	 */
	public String getString(int value) {
		return Integer.toString(value);
	}
	
	/**
	 * Gets the string.
	 *
	 * @param value the value
	 * @return the string
	 */
	public String getString(float value) {
		return Float.toString(value);
	}
	
	/**
	 * Gets the string.
	 *
	 * @param value the value
	 * @return the string
	 */
	public String getString(boolean value) {
		return Boolean.toString(value);
	}
	
	/**
	 * Gets the value.
	 *
	 * @param data the data
	 * @return the value
	 */
	public Object getValue(String data) {
		return data;
	}
	
	/**
	 * Gets the type of field.
	 *
	 * @param field the field
	 * @return the type of field
	 */
	public static DataType getTypeOfField(Field field) {

		DataType type = DataType.DEFAULT;
		Class<?> fieldType = field.getType();
		if (fieldType == String.class) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute.type() == DataType.BLOB) {
				type = DataType.BLOB;
			} else if (attribute.type() == DataType.CLOB) {
				type = DataType.CLOB;
			} else {
				type = DataType.DEFAULT;
			}
			type = DataType.DEFAULT;
		} else if (fieldType == Date.class) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute.type() == DataType.TIMESTAMP) {
				type = DataType.TIMESTAMP;
			} else if (attribute.type() == DataType.TIME) {
				type = DataType.TIME;
			} else {
				type = DataType.DATE;
			}
		} else if (fieldType == long.class || fieldType == Long.class) {
			type = DataType.LONG;
		} else if (fieldType == int.class || fieldType == Integer.class) {
			type = DataType.INT;
		} else if (fieldType == float.class || fieldType == Float.class) {
			type = DataType.FLOAT;
		} else if (fieldType == boolean.class || fieldType == Boolean.class) {
			type = DataType.BOOLEAN;
		} else if (fieldType == byte[].class) {
			type = DataType.BLOB;
		}

		log.debug("Field " + field.getName() + " is = " + type + "'");
		return type;
	}
}
