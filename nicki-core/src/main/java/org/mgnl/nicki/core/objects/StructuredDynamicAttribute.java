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
package org.mgnl.nicki.core.objects;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.methods.ListStructuredForeignKeyMethod;
import org.mgnl.nicki.core.methods.StructuredForeignKeyMethod;

@SuppressWarnings("serial")
public class StructuredDynamicAttribute extends DynamicAttribute implements Serializable {

	public StructuredDynamicAttribute(String name, String ldapName,	Class<?> attributeClass) {
		super(name, ldapName, attributeClass);
	}
	@Override
	public void init(NickiContext context, DynamicObject dynamicObject, ContextSearchResult rs) {
		if (isMultiple()) {
			List<Object> values = rs.getValues(getExternalName());
			dynamicObject.put(getName(), values);
			dynamicObject.put(getMultipleGetter(getName()),
					new ListStructuredForeignKeyMethod(context, rs, getExternalName(), getForeignKeyClass()));

		} else {
			String value = (String) rs.getValue(String.class, getExternalName());
			if (StringUtils.isNotEmpty(value)) {
				dynamicObject.put(getName(), value);
				dynamicObject.put(getGetter(getName()),
						new StructuredForeignKeyMethod(context, rs, getExternalName(), getForeignKeyClass()));
			}
		}
	}
}

