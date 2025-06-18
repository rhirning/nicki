
package org.mgnl.nicki.db.context.microsoft;

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


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.context.BaseDBContext;
import org.mgnl.nicki.db.context.DBContext;

import lombok.extern.slf4j.Slf4j;


/**
 * The Class SqlServerContext.
 */
@Slf4j
public class SqlServerContext
		extends BaseDBContext
		implements DBContext {

	/** The Constant TIMESTAMP_PATTERN. */
	public final static String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	/** The Constant PARTS_PATTERN. */
	public final static String PARTS_PATTERN = "yyyy, MM, dd, HH, mm, ss, SSS";
	
	/** The Constant DATE_PATTERN. */
	public final static String DATE_PATTERN = "yyyy-MM-dd";

	/**
	 * Gets the time stamp.
	 *
	 * @return the time stamp
	 */
	@Override
	public String getTimeStamp() {
		return "CURRENT_TIMESTAMP";
	}

	/**
	 * Gets the sys date.
	 *
	 * @return the sys date
	 */
	@Override
	public String getSysDate() {
		return "GETDATE";
	}
	
	/**
	 * Gets the now plus hours.
	 *
	 * @param hours the hours
	 * @return the now plus hours
	 */
	public String getNowPlusHours(int hours) {
		return "DATEADD(hour, " + hours + ", CURRENT_TIMESTAMP)}";
	}
	
	/**
	 * Gets the date value.
	 *
	 * @param bean the bean
	 * @param field the field
	 * @param attribute the attribute
	 * @return the date value
	 */
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
			log.error("Error creating date expression", e);
		}
		return null;
	}

	/**
	 * To timestamp.
	 *
	 * @param date the date
	 * @return the string
	 */
	@Override
	public String toTimestamp(Date date) {
		if (date != null) {
			return "DATETIMEFROMPARTS (" + new SimpleDateFormat(PARTS_PATTERN).format(date) + ")";
		} else {
			return null;
		}
	}


	/**
	 * To date.
	 *
	 * @param date the date
	 * @return the string
	 */
	@Override
	public String toDate(Date date) {
		return toTimestamp(date);
	}

	/**
	 * Gets the qualified name.
	 *
	 * @param name the name
	 * @return the qualified name
	 */
	@Override
	public String getQualifiedName(String name) {
		if (!StringUtils.contains(name, '.') && this.getSchema() != null) {
			return this.getSchema() + "." + name;
		} else {
			return name;
		}
	}

	/**
	 * Gets the date as db string.
	 *
	 * @param date the date
	 * @return the date as db string
	 */
	@Override
	public String getDateAsDbString(Date date) {
		if (date != null) {
			return new SimpleDateFormat(TIMESTAMP_PATTERN).format(date);
		} else {
			return null;
		}
	}
}
