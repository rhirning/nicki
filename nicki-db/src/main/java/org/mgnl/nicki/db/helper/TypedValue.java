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

@Data
@AllArgsConstructor
public class TypedValue {
	private Type type;
	private int pos;
	private Object rawValue;
	
	public void fillPreparedStatement(PreparedStatement pstmt) throws SQLException {
		type.fillPreparedStatement(pstmt, pos, rawValue);
	}
	
	public TypedValue correctValue(Object bean, String attributeName) {
		if (type == Type.STRING) {
			Attribute attribute = BeanHelper.getBeanAttribute(bean.getClass(), attributeName);
			if (attribute != null && attribute.length() > 0) {
				rawValue = StringUtils.rightPad((String) rawValue, attribute.length());
			}
		}
		return this;
	}
	
	public TypedValue correctValue(Class<?> beanClass, String attributeName) {
		if (type == Type.STRING) {
			Attribute attribute = BeanHelper.getBeanAttribute(beanClass, attributeName);
			if (attribute != null && attribute.length() > 0) {
				rawValue = StringUtils.rightPad((String) rawValue, attribute.length());
			}
		}
		return this;
	}
	
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
