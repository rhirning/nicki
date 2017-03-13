package org.mgnl.nicki.db.statistics;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import org.mgnl.nicki.core.helper.JsonHelper;
import org.mgnl.nicki.db.context.DBContext;

public class Variable {
	
	enum TYPE {
		STRING {
			@Override
			String toString(DBContext dbContext, String value) {
				return "'" + value + "'";
			}

			@Override
			String toString(ResultSet resultSet, String name) throws SQLException {
				return resultSet.getString(name);
			}
		},
		DATE {
			@Override
			String toString(DBContext dbContext, String value) throws ParseException {
				Date date = JsonHelper.dateFromDisplayDay(value);
				return dbContext.toDate(date);
			}

			@Override
			String toString(ResultSet resultSet, String name) throws SQLException {
				Date date = resultSet.getDate(name);
				return JsonHelper.getDisplayDay(date);
			}
		},
		TIMESTAMP {
			@Override
			String toString(DBContext dbContext, String value) throws ParseException {
				Date timestamp = JsonHelper.dateFromMilli(value);
				return dbContext.toDate(timestamp);
			}

			@Override
			String toString(ResultSet resultSet, String name) throws SQLException {
				Date date = resultSet.getTimestamp(name);
				return JsonHelper.getMilli(date);
			}
		},
		INTEGER {
			@Override
			String toString(DBContext dbContext, String value) throws ParseException {
				return value;
			}

			@Override
			String toString(ResultSet resultSet, String name) throws SQLException {
				return Integer.toString(resultSet.getInt(name));
			}
		};
		
		abstract String toString(DBContext dbContext, String value) throws ParseException;

		abstract String toString(ResultSet resultSet, String name) throws SQLException;
	};
	
	public String toString(DBContext dbContext, String value) throws ParseException {
		return getVariableType().toString(dbContext, value);
	}
	
	private String name;
	
	private String type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public TYPE getVariableType() {
		return TYPE.valueOf(type);
	}

	public String toString(ResultSet resultSet) throws SQLException {
		return getVariableType().toString(resultSet, name);
	}

}
