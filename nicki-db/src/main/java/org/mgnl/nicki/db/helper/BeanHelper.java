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


import java.lang.reflect.Field;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.data.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanHelper {
	private static final Logger LOG = LoggerFactory.getLogger(BeanHelper.class);

	
	public static String getFieldName(Class<?> beanClass, String columnName) {
		for (Field field : beanClass.getDeclaredFields()) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null && StringUtils.equalsIgnoreCase(columnName, attribute.name())) {
				return field.getName();
			}
		}
		return null;		
	}

	public static String getColumnName(Class<?> beanClass, String name) {
		for (Field field : beanClass.getDeclaredFields()) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null ) {
				return attribute.name();
			}
		}
		return null;		
	}
	
	public static Type getTypeOfField(Class<?> beanClass, String fieldName){

		Type type = Type.UNKONWN;
		try {
			Field field = beanClass.getDeclaredField(fieldName);
			Class<?> fieldType = field.getType();
			if (fieldType == String.class) {
				type = Type.STRING;
			} else if (fieldType == Date.class) {
				Attribute attribute = field.getAnnotation(Attribute.class);
				if (attribute.type() == DataType.TIMESTAMP) {
					type = Type.TIMESTAMP;
				} else {
					type = Type.DATE;
				}
			} else if (fieldType == long.class || fieldType == Long.class) {
				type = Type.LONG;
			} else if (fieldType == int.class || fieldType == Integer.class) {
				type = Type.INT;
			}
		} catch (NoSuchFieldException | SecurityException e) {
			LOG.error("Invalid name: " + beanClass.getName() + "." + fieldName);
		}
		
		LOG.debug("Field " + fieldName + " is = " + type + "'");
		return type;
	}
	
	public static Type getTypeOfColumn(Class<?> beanClass, String columnName){

		Type type = Type.UNKONWN;
		String fieldName = getFieldName(beanClass, columnName);
		if (fieldName != null) {
			type = getTypeOfField(beanClass, fieldName);
		}
		
		LOG.debug("Column " + columnName + " is = " + type + "'");
		return type;
	}
}
