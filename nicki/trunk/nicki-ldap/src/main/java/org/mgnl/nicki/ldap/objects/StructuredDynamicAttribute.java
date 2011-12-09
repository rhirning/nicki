/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
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

