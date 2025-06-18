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

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.helper.BeanUtilsHelper;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.annotation.ForeignKey;
import org.mgnl.nicki.db.context.DBContext;
import org.mgnl.nicki.db.context.DBContextManager;
import org.mgnl.nicki.db.data.DataType;
import org.mgnl.nicki.db.profile.InitProfileException;

import lombok.extern.slf4j.Slf4j;


/**
 * The Class BeanHelper.
 */
@Slf4j
public class BeanHelper {

	/**
	 * The Enum READONLY.
	 */
	public enum READONLY {
		
		/** The true. */
		TRUE, 
 /** The false. */
 FALSE
	}

	/**
	 * Gets the field name.
	 *
	 * @param beanClass the bean class
	 * @param columnName the column name
	 * @return the field name
	 */
	public static String getFieldName(Class<?> beanClass, String columnName) {
		Field field = getFieldFromColumnName(beanClass, columnName);
		return field != null ? field.getName() : null;
	}

	/**
	 * Gets the field from column name.
	 *
	 * @param beanClass the bean class
	 * @param columnName the column name
	 * @return the field from column name
	 */
	public static Field getFieldFromColumnName(Class<?> beanClass, String columnName) {
		for (Field field : beanClass.getDeclaredFields()) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null && StringUtils.equalsIgnoreCase(columnName, attribute.name())) {
				return field;
			}
		}
		
		return null;
	}

	/**
	 * Gets the fields.
	 *
	 * @param beanClass the bean class
	 * @return the fields
	 */
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

	/**
	 * Gets the column name.
	 *
	 * @param beanClass the bean class
	 * @param fieldName the field name
	 * @return the column name
	 */
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

	/**
	 * Gets the type of field.
	 *
	 * @param field the field
	 * @return the type of field
	 */
	public static Type getTypeOfField(Field field) {

		Type type = Type.UNKONWN;
		Class<?> fieldType = field.getType();
		if (fieldType == String.class) {
			type = Type.STRING;
		} else if (fieldType == Date.class) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute.type() == DataType.TIMESTAMP) {
				type = Type.TIMESTAMP;
			} else if (attribute.type() == DataType.TIME) {
				type = Type.TIME;
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

		log.debug("Field " + field.getName() + " is = " + type + "'");
		return type;
	}

	/**
	 * Gets the type of field.
	 *
	 * @param beanClass the bean class
	 * @param fieldName the field name
	 * @return the type of field
	 */
	public static Type getTypeOfField(Class<?> beanClass, String fieldName) {

		Type type = Type.UNKONWN;
		try {
			Field field = beanClass.getDeclaredField(fieldName);
			type = getTypeOfField(field);
		} catch (NoSuchFieldException | SecurityException e) {
			log.error("Invalid name: " + beanClass.getName() + "." + fieldName);
		}

		log.debug("Field " + fieldName + " is = " + type + "'");
		return type;
	}

	/**
	 * Gets the type of column.
	 *
	 * @param beanClass the bean class
	 * @param columnName the column name
	 * @return the type of column
	 */
	public static Type getTypeOfColumn(Class<?> beanClass, String columnName) {

		Type type = Type.UNKONWN;
		String fieldName = getFieldName(beanClass, columnName);
		if (fieldName != null) {
			type = getTypeOfField(beanClass, fieldName);
		}

		log.debug("Column " + columnName + " is = " + type + "'");
		return type;
	}

	/**
	 * Gets the bean attribute.
	 *
	 * @param beanClass the bean class
	 * @param fieldName the field name
	 * @return the bean attribute
	 */
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

	/**
	 * Gets the value.
	 *
	 * @param bean the bean
	 * @param fieldName the field name
	 * @return the value
	 */
	public static Object getValue(Object bean, String fieldName) {
		return BeanUtilsHelper.getProperty(bean, fieldName);
	}

	/**
	 * Sets the value.
	 *
	 * @param bean the bean
	 * @param fieldName the field name
	 * @param newValue the new value
	 */
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

	/**
	 * Gets the type.
	 *
	 * @param bean the bean
	 * @param fieldName the field name
	 * @return the type
	 * @throws NoSuchFieldException the no such field exception
	 * @throws SecurityException the security exception
	 */
	public static Class<?> getType(Object bean, String fieldName) throws NoSuchFieldException, SecurityException {
		return bean.getClass().getDeclaredField(fieldName).getType();
	}

	/**
	 * Adds the foreign key.
	 *
	 * @param bean the bean
	 * @param foreignObject the foreign object
	 */
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

	/**
	 * Gets the foreign key ids.
	 *
	 * @param bean the bean
	 * @return the foreign key ids
	 */
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

	/**
	 * Gets the foreign key.
	 *
	 * @param bean the bean
	 * @param attributeName the attribute name
	 * @return the foreign key
	 */
	public static ForeignKey getForeignKey(Object bean, String attributeName) {
		for (Field field : getFields(bean.getClass())) {
			ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
			if (foreignKey != null && StringUtils.equals(attributeName, field.getName())) {
				return foreignKey;
			}
		}
		return null;
	}

	/**
	 * Checks if is foreign key.
	 *
	 * @param bean the bean
	 * @param attributeName the attribute name
	 * @return true, if is foreign key
	 */
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

	/**
	 * Gets the key attribute.
	 *
	 * @param bean the bean
	 * @return the key attribute
	 */
	public static String getKeyAttribute(Object bean) {
		for (Field field : getFields(bean.getClass())) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null && attribute.primaryKey()) {
				return field.getName();
			}
		}
		return null;
	}

	/**
	 * Gets the foreign value.
	 *
	 * @param bean the bean
	 * @param attributeName the attribute name
	 * @param dbContextName the db context name
	 * @return the foreign value
	 */
	public static String getForeignValue(Object bean, String attributeName, String dbContextName) {
		for (Field field : getFields(bean.getClass())) {
			if (StringUtils.equals(attributeName, field.getName())) {
				ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
				if (foreignKey != null && StringUtils.isNotBlank(foreignKey.display())) {
					try {
						Object foreignObject = Classes.newInstance(foreignKey.foreignKeyClass());
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

	/**
	 * Gets the foreign key values.
	 *
	 * @param bean the bean
	 * @param attributeName the attribute name
	 * @param dbContextName the db context name
	 * @return the foreign key values
	 */
	public static List<Object> getForeignKeyValues(Object bean, String attributeName, String dbContextName) {
		for (Field field : getFields(bean.getClass())) {
			if (StringUtils.equals(attributeName, field.getName())) {
				ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
				if (foreignKey != null && StringUtils.isNotBlank(foreignKey.display())) {
					try {
						Object foreignObject = Classes.newInstance(foreignKey.foreignKeyClass());
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

	/**
	 * Gets the foreign key object.
	 *
	 * @param bean the bean
	 * @param attributeName the attribute name
	 * @param dbContextName the db context name
	 * @param key the key
	 * @return the foreign key object
	 */
	public static Object getForeignKeyObject(Object bean, String attributeName, String dbContextName, Long key) {
		for (Field field : getFields(bean.getClass())) {
			if (StringUtils.equals(attributeName, field.getName())) {
				ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
				if (foreignKey != null && StringUtils.isNotBlank(foreignKey.display())) {
					try {
						Object foreignObject = Classes.newInstance(foreignKey.foreignKeyClass());
						Field foreignKeyField = getFieldFromColumnName(foreignKey.foreignKeyClass(), foreignKey.columnName());
						if (foreignKeyField != null) {
							setValue(foreignObject, foreignKeyField.getName(), key);
							try (DBContext dbContext = DBContextManager.getContext(dbContextName)) {
								return dbContext.loadObject(foreignObject, false);
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

	/**
	 * Import csv.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @param lines the lines
	 * @param separator the separator
	 * @return the list
	 */
	public static <T> List<T> importCsv(Class<T> clazz, List<String> lines, String separator) {
		String line  = lines.get(0);
		String[] parts = DataHelper.toStringArray(line, separator);
		List<T> beans = new ArrayList<T>();
		int numberColumns = parts.length;
		int numberRows = lines.size();
		String[] titles = new String[numberColumns];		
		DataType[] types = new DataType[numberColumns];
		for (int i = 0; i < numberColumns; i++) {
			String title = parts[i];
			try {
				Field field = clazz.getDeclaredField(title);
				titles[i] = title;
				types[i] = DataType.getTypeOfField(field);
			} catch (NoSuchFieldException | SecurityException e) {
				log.error("Error importing field: " + title );
			}
		}
		
		for (int rowNum = 1 ; rowNum < numberRows; rowNum++) {
			T bean;
			try {
				bean = Classes.newInstance(clazz);
			} catch (InstantiationException | IllegalAccessException e) {
				log.error("Error creating Bean: " + clazz, e);
				return beans;
			}
			line  = lines.get(rowNum);
			parts = DataHelper.toStringArray(line, separator);
			for (int i = 0; i < numberColumns; i++) {
				if (titles[i] != null && parts.length > i) {
					BeanHelper.setValue(bean, titles[i], types[i].getValue(parts[i]));
				}
			}
			beans.add(bean);
		}

		return beans;
	}
	
	/**
	 * Gets the name.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @param attributeName the attribute name
	 * @return the name
	 */
	public static <T> String getName(Class<T> clazz, String attributeName) {
		String key = clazz.getName()
			+ "."
			+ attributeName;
		String name = I18n.getText(key);
		if (isMandatory(clazz, attributeName)) {
			name += " *";
		}

		return name;
	}
	
	/**
	 * Checks if is mandatory.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @param attributeName the attribute name
	 * @return true, if is mandatory
	 */
	public static <T> boolean isMandatory(Class<T> clazz, String attributeName) {
		for (Field field : clazz.getDeclaredFields()) {
			if (StringUtils.equals(attributeName, field.getName())) {
				Attribute attribute = field.getAnnotation(Attribute.class);
				if (attribute != null && attribute.mandatory()) {
					return true;
				}
			}
		}
		return false;
	}
	
}
