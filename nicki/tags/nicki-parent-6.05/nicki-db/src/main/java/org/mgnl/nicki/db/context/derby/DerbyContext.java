package org.mgnl.nicki.db.context.derby;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.math.RandomUtils;
import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.context.BaseDBContext;
import org.mgnl.nicki.db.context.DBContext;
import org.mgnl.nicki.db.context.PrimaryKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DerbyContext
		extends BaseDBContext
		implements DBContext {

	private static final Logger LOG = LoggerFactory.getLogger(DerbyContext.class);
	public final static String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static SimpleDateFormat timestampFormat = new SimpleDateFormat(TIMESTAMP_PATTERN);

	public final static String DATE_PATTERN = "yyyy-MM-dd";
	public static SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);

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
			return getDateValue(date, attribute);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			LOG.error("Error creating date expression", e);
		}
		return null;
	}

	@Override
	public String toTimestamp(Date date) {
		if (date != null) {
			synchronized (timestampFormat) {
				return "timestamp('" + timestampFormat.format(date) + "')";
			}
		} else {
			return null;
		}
	}


	@Override
	public String toDate(Date date) {
		if (date != null) {
			synchronized (dateFormat) {
				return "'" + dateFormat.format(date) + "'";
			}
		} else {
			return null;
		}
	}

	@Override
	public PrimaryKey getSequenceNumber(String sequenceName) throws Exception {
		
		return new PrimaryKey(RandomUtils.nextInt());
	}
}
