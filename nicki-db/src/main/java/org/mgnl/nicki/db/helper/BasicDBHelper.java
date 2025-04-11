
package org.mgnl.nicki.db.helper;

import java.lang.reflect.Field;

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


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.annotation.Table;
import org.mgnl.nicki.db.context.DBContext;
import org.mgnl.nicki.db.handler.IsExistSelectHandler;
import org.mgnl.nicki.db.handler.MaxIntValueSelectHandler;
import org.mgnl.nicki.db.handler.SelectHandler;
import org.mgnl.nicki.db.handler.SequenceValueSelectHandler;
import org.mgnl.nicki.db.profile.DBProfile;

import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class BasicDBHelper.
 */
@Slf4j
public class BasicDBHelper {
	
	/** The Constant DATE_FORMAT. */
	public final static String DATE_FORMAT = "yyyy-MM-dd";
	
	/** The Constant COLUMN_SEPARATOR. */
	public final static String COLUMN_SEPARATOR = ", ";

	/** The allow prepared where map. */
	private static Map<Class<?>, Boolean> allowPreparedWhereMap = new HashMap<Class<?>, Boolean>();
	
	/** The trim strings map. */
	private static Map<String, Boolean> trimStringsMap = new HashMap<String, Boolean>();

	/**
	 * Checks if is allow prepared where.
	 *
	 * @param dbContext the db context
	 * @return true, if is allow prepared where
	 */
	public static boolean isAllowPreparedWhere(DBContext dbContext) {
		if (!allowPreparedWhereMap.containsKey(dbContext.getClass())) {
			allowPreparedWhereMap.put(dbContext.getClass(), Config.getBoolean(dbContext.getClass().getName() + ".allowPreparedWhere", true));
		}
		return allowPreparedWhereMap.get(dbContext.getClass()).booleanValue();
	}
	
	/**
	 * Bestimmt, ob in where-Bedingungen String-Werte in trim() eingepackt werden
	 * Es kann konfiguriert werden (aufsteigende Priorität):
	 * <ul>
	 * <li>contextClass.getName() + ".trimStrings" (default false)
	 * <li>Mit Argument trimStrings in Table-Annotation ("true" oder "false"). Wird nur berücksichtigt, falls trimStrings gesetzt ist
	 * <li>beanClass.getName() + ".trimStrings". Wird nur berücksichtigt, falls ein Wert gesetzt ist
	 * <li>Mit Argument trim in Attribute-Annotation ("true" oder "false"). Wird nur berücksichtigt, falls trim gesetzt ist
	 *
	 * @param contextClass the context class
	 * @param beanClass the bean class
	 * @param field the field
	 * @return true, if is trim strings
	 */
	
	
	public static boolean isTrimStrings(Class<?> contextClass, Class<?> beanClass, Field field) {
		String key = contextClass.getName() + ":" + beanClass.getName() + ":" + field.getName();
		if (!trimStringsMap.containsKey(key)) {
			boolean result = Config.getBoolean(contextClass.getName() + ".trimStrings", false);
			Table tableAnnotation = beanClass.getAnnotation(Table.class);
			if (tableAnnotation != null && StringUtils.isNotBlank(tableAnnotation.trimStrings())) {
				result = DataHelper.booleanOf(tableAnnotation.trimStrings());
			}
			String beanConfig = Config.getString(beanClass.getName() + ".trimStrings");
			if (StringUtils.isNotBlank(beanConfig)) {
				result = DataHelper.booleanOf(beanConfig);
			}
			Attribute attributeAnnotation = field.getAnnotation(Attribute.class);
			if (attributeAnnotation != null && StringUtils.isNotBlank(attributeAnnotation.trim())) {
				result = DataHelper.booleanOf(attributeAnnotation.trim());
			}
			trimStringsMap.put(key, result);
		}
		return trimStringsMap.get(key).booleanValue();
	}

	/**
	 * Execute update.
	 *
	 * @param profile the profile
	 * @param statement the statement
	 * @throws Exception the exception
	 */
	public static void executeUpdate(DBProfile profile, String statement) throws Exception {

		try(
			Connection conn = profile.getConnection();
			Statement stmt = conn.createStatement()
		) {
			log.debug(statement);
			stmt.executeUpdate(statement);
		}
	}
	
	/**
	 * Execute delete.
	 *
	 * @param profile the profile
	 * @param tableName the table name
	 * @param whereClause the where clause
	 * @throws Exception the exception
	 */
	public static void executeDelete(DBProfile profile, String tableName, String whereClause) throws Exception {
		executeUpdate(profile, "delete from " + tableName + " where " + whereClause);
	}


	/**
	 * Checks if is exist.
	 *
	 * @param profile the profile
	 * @param tableName the table name
	 * @param whereClause the where clause
	 * @return true, if is exist
	 */
	public static boolean isExist(DBProfile profile, String tableName, String whereClause) {
		IsExistSelectHandler handler = new IsExistSelectHandler(tableName, whereClause);
		try {
			select(profile, handler);
		} catch (Exception e) {
			log.error("Error", e);
		}
		return handler.isExist();
	}
	
	/**
	 * Select.
	 *
	 * @param profile the profile
	 * @param handler the handler
	 * @throws Exception the exception
	 */
	public static void select(DBProfile profile, SelectHandler handler) throws Exception {

		if (handler.isLoggingEnabled()) {
			log.debug(handler.getSearchStatement());
		}

		try(
			Connection conn = profile.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(handler.getSearchStatement())
		) {
			handler.handle(rs);
		}
	}

	/**
	 * Gets the unique int.
	 *
	 * @param profile the profile
	 * @param tableName the table name
	 * @param column the column
	 * @return the unique int
	 */
	public static int getUniqueInt(DBProfile profile, String tableName, String column) {
		if (!isExist(profile, tableName, "1=1")) {
			return 1;
		}
		MaxIntValueSelectHandler handler = new MaxIntValueSelectHandler(tableName, column);
		try {
			select(profile, handler);
		} catch (Exception e) {
			log.error("Error", e);
		}
		return handler.getResult();
	}
	
	/**
	 * Gets the order sequence number.
	 *
	 * @param profile the profile
	 * @param sequence the sequence
	 * @return the order sequence number
	 * @throws Exception the exception
	 */
	public static long getOrderSequenceNumber(DBProfile profile, String sequence) throws Exception {
		SequenceValueSelectHandler handler = new SequenceValueSelectHandler(sequence);
		select(profile, handler);
		return handler.getResult();
	}

	/**
	 * To string.
	 *
	 * @param date the date
	 * @return the string
	 */
	public static String toString(Date date) {
		return new SimpleDateFormat(DATE_FORMAT).format(date);
		
	}

	/**
	 * To date.
	 *
	 * @param date the date
	 * @return the string
	 */
	public static String toDate(Date date) {
		return "to_date('" + new SimpleDateFormat(DATE_FORMAT).format(date) + "','" + DATE_FORMAT + "')";
		
	}

	/**
	 * Execute insert.
	 *
	 * @param profile the profile
	 * @param tableName the table name
	 * @param values the values
	 * @throws Exception the exception
	 */
	public static void executeInsert(DBProfile profile, String tableName,
			Map<String, String> values) throws Exception {
		executeUpdate(profile, getInsertStatement(tableName, values));
	}

	/**
	 * Gets the insert statement.
	 *
	 * @param tableName the table name
	 * @param columnValues the column values
	 * @return the insert statement
	 */
	protected static String getInsertStatement(String tableName, Map<String, String> columnValues) {
		ColumnsAndValues cv = new ColumnsAndValues("","");
		cv = addStringValues(cv, columnValues);
		return "insert into " + tableName + " (" + cv.getColumns() + ") values (" + cv.getValues() + ")";
	}
	
	/**
	 * Adds the string values.
	 *
	 * @param columnsAndValues the columns and values
	 * @param columnValues the column values
	 * @return the columns and values
	 */
	protected static ColumnsAndValues addStringValues(ColumnsAndValues columnsAndValues, Map<String, String> columnValues) {
		for (String  columnName : columnValues.keySet()) {
			columnsAndValues.add(columnName, columnValues.get(columnName));
		}
		return columnsAndValues;
	}

}
