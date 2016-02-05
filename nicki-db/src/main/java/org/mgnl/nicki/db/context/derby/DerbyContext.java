package org.mgnl.nicki.db.context.derby;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.context.BaseDBContext;
import org.mgnl.nicki.db.context.DBContext;
import org.mgnl.nicki.db.context.NotSupportedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DerbyContext
		extends BaseDBContext
		implements DBContext {

	private static final Logger LOG = LoggerFactory.getLogger(DerbyContext.class);

	@Override
	public String getTimeStamp() {
		return "CURRENT_TIMESTAMP";
	}

	@Override
	public String getSysDate() {
		return "CURRENT_DATE";
	}
	
	@Override
	protected String getDateValue(Object bean, Field field, Attribute attribute) {
		if (attribute.now()) {
			return "CURRENT_TIMESTAMP";
		}
		try {
			Date date = (Date) this.getValue(bean, field);
			return this.toTimestamp(date);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected <T> Long _create(T bean) throws SQLException, NotSupportedException {
		try (Statement stmt = this.getConnection().createStatement()) {
			String statement = this.createInsertStatement(bean);
			LOG.debug(statement);
			String generatedColumns[] = this.getGeneratedKeys(bean);
			if (generatedColumns != null) {
				stmt.executeUpdate(statement, generatedColumns);
				ResultSet generatedKeys = stmt.getGeneratedKeys();
				if (generatedKeys != null && generatedKeys.next()) {
					return new Long(generatedKeys.getLong(1));
				} else {
					return null;
				}
			} else {
				stmt.executeUpdate(statement);
				return null;
			}
		}

	}

	@Override
	public String toTimestamp(Date date) {
		if (date != null) {
			return "timestamp('" + timestampOracle.format(date) + "')";
		} else {
			return "''";
		}
	}
}
