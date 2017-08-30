/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.db.context.oracle;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.mgnl.nicki.db.annotation.Attribute;
import org.mgnl.nicki.db.context.BaseDBContext;
import org.mgnl.nicki.db.context.DBContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OracleContext
		extends BaseDBContext
		implements DBContext {

	private static final Logger LOG = LoggerFactory.getLogger(OracleContext.class);

	@Override
	protected String getDateValue(Object bean, Field field, Attribute attribute) {
		if (attribute.now()) {
			return "SYSTIMESTAMP";
		}
		try {
			Date date = (Date) this.getValue(bean, field);
			return this.toTimestamp(date);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			LOG.error("Could not parse date", e);
		}

		return null;
	}

	@Override
	public String toTimestamp(Date date) {
		if (date != null) {
			return "to_date('" + new SimpleDateFormat(TIMESTAMP_FOR_ORACLE).format(date) + "','" + TIMESTAMP_ORACLE + "')";
		} else {
			return null;
		}
	}
}
