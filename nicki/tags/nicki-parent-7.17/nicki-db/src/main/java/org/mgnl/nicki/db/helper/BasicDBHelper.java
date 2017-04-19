/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
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
