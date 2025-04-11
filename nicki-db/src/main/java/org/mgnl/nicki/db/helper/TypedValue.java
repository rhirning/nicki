package org.mgnl.nicki.db.helper;

import java.lang.reflect.Field;

/*-
 * #%L
 * nicki-db
 * %%
 * Copyright (C) 2017 - 2023 Ralf Hirning
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

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.db.annotation.Attribute;

import lombok.AllArgsConstructor;
import lombok.Data;

// TODO: Auto-generated Javadoc
/**
 * The Class TypedValue.
 */
@Data
@AllArgsConstructor
public class TypedValue {
	
	/** The type. */
	private Type type;
	
	/** The pos. */
	private int pos;
	
	/** The raw value. */
	private Object rawValue;
	
	/**
	 * Fill prepared statement.
	 *
	 * @param pstmt the pstmt
	 * @throws SQLException the SQL exception
	 */
	public void fillPreparedStatement(PreparedStatement pstmt) throws SQLException {
		type.fillPreparedStatement(pstmt, pos, rawValue);
	}
	
	/**
	 * Correct value.
	 *
	 * @param bean the bean
	 * @param attributeName the attribute name
	 * @return the typed value
	 */
	public TypedValue correctValue(Object bean, String attributeName) {
		if (type == Type.STRING) {
			Attribute attribute = BeanHelper.getBeanAttribute(bean.getClass(), attributeName);
			if (attribute != null && attribute.length() > 0) {
				rawValue = StringUtils.rightPad((String) rawValue, attribute.length());
			}
		}
		return this;
	}
	
	/**
	 * Correct value.
	 *
	 * @param beanClass the bean class
	 * @param attributeName the attribute name
	 * @return the typed value
	 */
	public TypedValue correctValue(Class<?> beanClass, String attributeName) {
		if (type == Type.STRING) {
			Attribute attribute = BeanHelper.getBeanAttribute(beanClass, attributeName);
			if (attribute != null && attribute.length() > 0) {
				rawValue = StringUtils.rightPad((String) rawValue, attribute.length());
			}
		}
		return this;
	}
	
	/**
	 * Correct value.
	 *
	 * @param field the field
	 * @return the typed value
	 */
	public TypedValue correctValue(Field field) {
		if (type == Type.STRING) {
			Attribute attribute = field.getAnnotation(Attribute.class);
			if (attribute != null && attribute.length() > 0) {
				rawValue = StringUtils.rightPad((String) rawValue, attribute.length());
			}
		}
		return this;
	}
}
