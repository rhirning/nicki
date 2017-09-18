
package org.mgnl.nicki.db.context.derby;

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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.context.BaseDBContext;
import org.mgnl.nicki.db.context.DBContext;
import org.mgnl.nicki.db.context.PrimaryKey;
import org.mgnl.nicki.db.handler.SequenceValueSelectHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DerbyContext
		extends BaseDBContext
		implements DBContext {

	private static final Logger LOG = LoggerFactory.getLogger(DerbyContext.class);
	public final static String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public final static String DATE_PATTERN = "yyyy-MM-dd";

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
			return "timestamp('" + new SimpleDateFormat(TIMESTAMP_PATTERN).format(date) + "')";
		} else {
			return null;
		}
	}


	@Override
	public String toDate(Date date) {
		if (date != null) {
			return "'" + new SimpleDateFormat(DATE_PATTERN).format(date) + "'";
		} else {
			return null;
		}
	}

	@Override
	public PrimaryKey getSequenceNumber(String sequenceName) throws Exception {

		SequenceValueSelectHandler handler = new DerbySequenceValueSelectHandler(getQualifiedName(sequenceName));
		select(handler);
		return new PrimaryKey(handler.getResult());
	}
	
	public String getQualifiedName(String name) {
		if (!StringUtils.contains(name, '.') && this.getSchema() != null) {
			return this.getSchema() + "." + name;
		} else {
			return name;
		}
	}
	
	public PrimaryKey XXXgetSequenceNumber(String sequenceName) throws Exception {
		
		return new PrimaryKey(RandomUtils.nextInt());
	}
}
