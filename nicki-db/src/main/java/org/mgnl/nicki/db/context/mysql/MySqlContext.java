package org.mgnl.nicki.db.context.mysql;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.context.BaseDBContext;
import org.mgnl.nicki.db.context.DBContext;

public class MySqlContext extends BaseDBContext implements DBContext {


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

	public String toTimestamp(Date date) {
		if (date != null) {
			return "to_date('" + new SimpleDateFormat(TIMESTAMP_FOR_ORACLE).format(date) + "','" + TIMESTAMP_ORACLE + "')";
		} else {
			return "''";
		}
	}
}
