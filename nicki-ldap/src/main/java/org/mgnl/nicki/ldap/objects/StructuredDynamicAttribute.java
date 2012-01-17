/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
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
 */
package org.mgnl.nicki.ldap.objects;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.methods.ListStructuredForeignKeyMethod;
import org.mgnl.nicki.ldap.methods.StructuredForeignKeyMethod;

@SuppressWarnings("serial")
public class StructuredDynamicAttribute extends DynamicAttribute implements Serializable {

	public StructuredDynamicAttribute(String name, String ldapName,	Class<String> attributeClass) {
		super(name, ldapName, attributeClass);
	}
	@Override
	public void init(NickiContext context, DynamicObject dynamicObject, ContextSearchResult rs) {
		if (isMultiple()) {
			List<Object> values = LdapHelper.getAttributes(rs, getLdapName());
			dynamicObject.put(getName(), values);
			dynamicObject.put(getMultipleGetter(getName()),
					new ListStructuredForeignKeyMethod(context, rs, getLdapName(), getForeignKeyClass()));

		} else {
			String value = (String) LdapHelper.getAttribute(rs, getLdapName());
			if (StringUtils.isNotEmpty(value)) {
				dynamicObject.put(getName(), value);
				dynamicObject.put(getGetter(getName()),
						new StructuredForeignKeyMethod(context, rs, getLdapName(), getForeignKeyClass()));
			}
		}
	}
}

