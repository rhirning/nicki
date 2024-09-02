
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

@Slf4j
public class BasicDBHelper {
	public final static String DATE_FORMAT = "yyyy-MM-dd";
	public final static String COLUMN_SEPARATOR = ", ";

	private static Map<Class<?>, Boolean> allowPreparedWhereMap = new HashMap<Class<?>, Boolean>();
	private static Map<String, Boolean> trimStringsMap = new HashMap<String, Boolean>();

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
	 * @param contextClass
	 * @param beanClass
	 * @param field
	 * @return
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

	public static void executeUpdate(DBProfile profile, String statement) throws Exception {

		try(
			Connection conn = profile.getConnection();
			Statement stmt = conn.createStatement()
		) {
			log.debug(statement);
			stmt.executeUpdate(statement);
		}
	}
	
	public static void executeDelete(DBProfile profile, String tableName, String whereClause) throws Exception {
		executeUpdate(profile, "delete from " + tableName + " where " + whereClause);
	}


	public static boolean isExist(DBProfile profile, String tableName, String whereClause) {
		IsExistSelectHandler handler = new IsExistSelectHandler(tableName, whereClause);
		try {
			select(profile, handler);
		} catch (Exception e) {
			log.error("Error", e);
		}
		return handler.isExist();
	}
	
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
	
	public static long getOrderSequenceNumber(DBProfile profile, String sequence) throws Exception {
		SequenceValueSelectHandler handler = new SequenceValueSelectHandler(sequence);
		select(profile, handler);
		return handler.getResult();
	}

	public static String toString(Date date) {
		return new SimpleDateFormat(DATE_FORMAT).format(date);
		
	}

	public static String toDate(Date date) {
		return "to_date('" + new SimpleDateFormat(DATE_FORMAT).format(date) + "','" + DATE_FORMAT + "')";
		
	}

	public static void executeInsert(DBProfile profile, String tableName,
			Map<String, String> values) throws Exception {
		executeUpdate(profile, getInsertStatement(tableName, values));
	}

	protected static String getInsertStatement(String tableName, Map<String, String> columnValues) {
		ColumnsAndValues cv = new ColumnsAndValues("","");
		cv = addStringValues(cv, columnValues);
		return "insert into " + tableName + " (" + cv.getColumns() + ") values (" + cv.getValues() + ")";
	}
	
	protected static ColumnsAndValues addStringValues(ColumnsAndValues columnsAndValues, Map<String, String> columnValues) {
		for (String  columnName : columnValues.keySet()) {
			columnsAndValues.add(columnName, columnValues.get(columnName));
		}
		return columnsAndValues;
	}

}
