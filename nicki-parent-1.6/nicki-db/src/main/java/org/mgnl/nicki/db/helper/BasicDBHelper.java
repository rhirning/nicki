package org.mgnl.nicki.db.helper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.mgnl.nicki.db.handler.IsExistSelectHandler;
import org.mgnl.nicki.db.handler.MaxIntValueSelectHandler;
import org.mgnl.nicki.db.handler.SelectHandler;
import org.mgnl.nicki.db.handler.SequenceValueSelectHandler;

public class BasicDBHelper {
	public final static String DATE_FORMAT = "yyyy-MM-dd";
	public final static String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss Z";
	public static SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
	public static SimpleDateFormat timestampFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);

	public static void executeUpdate(DBProfile profile, String statement) throws Exception {
		Connection conn = null;
		Statement stmt = null; // Or PreparedStatement if needed
		try {
			conn = profile.getConnection();
			stmt = conn.createStatement();
			System.out.println(statement);
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
			e.printStackTrace();
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
				System.out.println(handler.getSearchStatement());
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
			e.printStackTrace();
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

}
