/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.db.helper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.mgnl.nicki.db.handler.IsExistSelectHandler;
import org.mgnl.nicki.db.handler.MaxIntValueSelectHandler;
import org.mgnl.nicki.db.handler.SelectHandler;
import org.mgnl.nicki.db.handler.SequenceValueSelectHandler;
import org.mgnl.nicki.db.profile.DBProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicDBHelper {
	private static final Logger LOG = LoggerFactory.getLogger(BasicDBHelper.class);
	public final static String DATE_FORMAT = "yyyy-MM-dd";
	public final static String COLUMN_SEPARATOR = ", ";

	public static void executeUpdate(DBProfile profile, String statement) throws Exception {

		try(
			Connection conn = profile.getConnection();
			Statement stmt = conn.createStatement()
		) {
			LOG.debug(statement);
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
			LOG.error("Error", e);
		}
		return handler.isExist();
	}
	
	public static void select(DBProfile profile, SelectHandler handler) throws Exception {

		if (handler.isLoggingEnabled()) {
			LOG.debug(handler.getSearchStatement());
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
			LOG.error("Error", e);
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
