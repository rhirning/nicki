package org.mgnl.nicki.db.context.oracle;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.context.BaseDBContext;
import org.mgnl.nicki.db.context.DBContext;

public class OracleContext extends BaseDBContext implements DBContext {


	protected String getDateValue(Object bean, Field field, Attribute attribute) {
		if (attribute.now()) {
			return "NOW()";
		}
		try {
			Date date = (Date) getValue(bean, field);
			return toTimestamp(date);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO Auto-generated method stub
		return null;
	}

	protected String toTimestamp(Date date) {
		return "to_date('" + timestampOracle.format(date) + "','" + TIMESTAMP_ORACLE + "')";
	}
}
