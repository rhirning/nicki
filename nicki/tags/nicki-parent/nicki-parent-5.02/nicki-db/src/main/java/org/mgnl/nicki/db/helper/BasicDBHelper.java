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
import java.sql.SQLException;
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
	public final static String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss Z";
	public static SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
	public static SimpleDateFormat timestampFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
	public final static String COLUMN_SEPARATOR = ", ";

	public static void executeUpdate(DBProfile profile, String statement) throws Exception {
		Connection conn = null;
		Statement stmt = null; // Or PreparedStatement if needed
		try {
			conn = profile.getConnection();
			stmt = conn.createStatement();
			LOG.debug(statement);
			stmt.executeUpdate(statement);
			stmt.close();
			stmt = null;
			conn.close(); // Return to connection pool
			conn = null; // Make sure we don't close it twice
		} finally {
			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					;
				}
				stmt = null;
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					;
				}
				conn = null;
			}
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

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = profile.getConnection();
			stmt = conn.createStatement();
			if (handler.isLoggingEnabled()) {
				LOG.debug(handler.getSearchStatement());
			}
			rs = stmt.executeQuery(handler.getSearchStatement());
			handler.handle(rs);
			rs.close();
			rs = null;
			
		} finally {
			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					;
				}
				stmt = null;
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					;
				}
				conn = null;
			}
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
	
	public static int getOrderSequenceNumber(DBProfile profile, String sequence) throws Exception {
		SequenceValueSelectHandler handler = new SequenceValueSelectHandler(sequence);
		select(profile, handler);
		return handler.getResult();
	}

	public static String toString(Date date) {
		return format.format(date);
		
	}

	public static String toDate(Date date) {
		return "to_date('" + format.format(date) + "','" + DATE_FORMAT + "')";
		
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
