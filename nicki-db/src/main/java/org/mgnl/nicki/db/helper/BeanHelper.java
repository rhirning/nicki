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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.helper.BeanUtilsHelper;
import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.annotation.ForeignKey;
import org.mgnl.nicki.db.context.DBContext;
import org.mgnl.nicki.db.context.DBContextManager;
import org.mgnl.nicki.db.data.DataType;
import org.mgnl.nicki.db.profile.InitProfileException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BeanHelper {

	public enum READONLY {
		TRUE, FALSE
	}

	public static String getFieldName(Class<?> beanClass, String columnName) {
		Field field = getFieldFromColumnName(beanClass, columnName);
		return field != null ? field.getName() : null;
	}

	public static Field getFieldFromColumnName(Class<?> beanClass, String columnName) {
		for (Field field : beanClass.getDeclaredFields()) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null && StringUtils.equalsIgnoreCase(columnName, attribute.name())) {
				return field;
			}
		}
		
		return null;
	}

	public static List<Field> getFields(Class<?> beanClass) {
		List<Field> fields = new ArrayList<>();
		for (Field field : beanClass.getDeclaredFields()) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null) {
				fields.add(field);
			}
		}
		return fields;
	}

	public static String getColumnName(Class<?> beanClass, String fieldName) {
		Field field;
		try {
			field = beanClass.getDeclaredField(fieldName);
		} catch (NoSuchFieldException | SecurityException e) {
			field = null;
		}
		if (field != null) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null && StringUtils.equalsIgnoreCase(fieldName, field.getName())) {
				return attribute.name();
			}
		}
		return null;
	}

	public static Type getTypeOfField(Class<?> beanClass, String fieldName) {

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
			} else if (fieldType == float.class || fieldType == Float.class) {
				type = Type.FLOAT;
			} else if (fieldType == boolean.class || fieldType == Boolean.class) {
				type = Type.BOOLEAN;
			} else if (fieldType == byte[].class) {
				type = Type.BLOB;
			}
		} catch (NoSuchFieldException | SecurityException e) {
			log.error("Invalid name: " + beanClass.getName() + "." + fieldName);
		}

		log.debug("Field " + fieldName + " is = " + type + "'");
		return type;
	}

	public static Type getTypeOfColumn(Class<?> beanClass, String columnName) {

		Type type = Type.UNKONWN;
		String fieldName = getFieldName(beanClass, columnName);
		if (fieldName != null) {
			type = getTypeOfField(beanClass, fieldName);
		}

		log.debug("Column " + columnName + " is = " + type + "'");
		return type;
	}

	public static Attribute getBeanAttribute(Class<?> beanClass, String fieldName) {
		Field field;
		try {
			field = beanClass.getDeclaredField(fieldName);
		} catch (NoSuchFieldException | SecurityException e) {
			field = null;
		}
		if (field != null) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null && StringUtils.equalsIgnoreCase(fieldName, field.getName())) {
				return attribute;
			}
		}
		return null;
	}

	public static Object getValue(Object bean, String fieldName) {
		return BeanUtilsHelper.getProperty(bean, fieldName);
	}

	public static void setValue(Object bean, String fieldName, Object newValue) {
		Field field;
		try {
			field = bean.getClass().getDeclaredField(fieldName);
		} catch (NoSuchFieldException | SecurityException e) {
			field = null;
		}
		if (field != null) {
			BeanUtilsHelper.setProperty(bean, field, newValue);
		}
	}

	public static Class<?> getType(Object bean, String fieldName) throws NoSuchFieldException, SecurityException {
		return bean.getClass().getDeclaredField(fieldName).getType();
	}

	public static void addForeignKey(Object bean, Object foreignObject) {
		if (foreignObject != null) {
			for (Field field : getFields(bean.getClass())) {
				ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
				if (foreignKey != null && foreignKey.foreignKeyClass().isAssignableFrom(foreignObject.getClass())) {
					Field foreignKeyField = getFieldFromColumnName(foreignObject.getClass(), foreignKey.columnName());
					if (foreignKeyField != null) {
						setValue(bean, field.getName(),
								getValue(foreignObject, foreignKeyField.getName()));
					}
				}
			}
		}
	}

	public static String[] getForeignKeyIds(Object bean) {
		List<String> foreignKeyIds = new ArrayList<>();
		for (Field field : getFields(bean.getClass())) {
			ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
			if (foreignKey != null) {
				foreignKeyIds.add(field.getName());
			}
		}
		return foreignKeyIds.toArray(new String[0]);
	}

	public static ForeignKey getForeignKey(Object bean, String attributeName) {
		for (Field field : getFields(bean.getClass())) {
			ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
			if (foreignKey != null && StringUtils.equals(attributeName, field.getName())) {
				return foreignKey;
			}
		}
		return null;
	}

	public static boolean isForeignKey(Object bean, String attributeName) {
		for (Field field : getFields(bean.getClass())) {
			if (StringUtils.equals(attributeName, field.getName())) {
				ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
				if (foreignKey != null) {
					return true;
				}
			}
		}
		return false;
	}

	public static String getKeyAttribute(Object bean) {
		for (Field field : getFields(bean.getClass())) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null && attribute.primaryKey()) {
				return field.getName();
			}
		}
		return null;
	}

	public static String getForeignValue(Object bean, String attributeName, String dbContextName) {
		for (Field field : getFields(bean.getClass())) {
			if (StringUtils.equals(attributeName, field.getName())) {
				ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
				if (foreignKey != null && StringUtils.isNotBlank(foreignKey.display())) {
					try {
						Object foreignObject = foreignKey.foreignKeyClass().newInstance();
						Field foreignField = getFieldFromColumnName(foreignObject.getClass(), foreignKey.columnName());
						Object keyValue = getValue(bean, attributeName);
						if (keyValue != null) {
							setValue(foreignObject, foreignField.getName(), keyValue);
							try (DBContext dbContext = DBContextManager.getContext(dbContextName)) {
								foreignObject = dbContext.loadObject(foreignObject, false);
								if (foreignObject != null) {
									return (String) getValue(foreignObject, foreignKey.display());
								}
							}
						}
					} catch (InstantiationException | IllegalAccessException | SQLException | InitProfileException e) {
						log.error("Error reading foreign value", e);
					}
				}
			}
		}
		return null;
	}

	public static List<Object> getForeignKeyValues(Object bean, String attributeName, String dbContextName) {
		for (Field field : getFields(bean.getClass())) {
			if (StringUtils.equals(attributeName, field.getName())) {
				ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
				if (foreignKey != null && StringUtils.isNotBlank(foreignKey.display())) {
					try {
						Object foreignObject = foreignKey.foreignKeyClass().newInstance();
						try (DBContext dbContext = DBContextManager.getContext(dbContextName)) {
							return dbContext.loadObjects(foreignObject, false);
						}
					} catch (InstantiationException | IllegalAccessException | SQLException | InitProfileException e) {
						log.error("Error reading foreign value", e);
					}
				}
			}
		}
		return new ArrayList<>();
	}
}
