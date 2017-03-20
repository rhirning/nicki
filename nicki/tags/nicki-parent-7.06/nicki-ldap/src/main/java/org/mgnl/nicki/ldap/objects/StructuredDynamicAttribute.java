/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.objects;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.methods.ListStructuredForeignKeyMethod;
import org.mgnl.nicki.core.methods.StructuredForeignKeyMethod;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicObject;

@SuppressWarnings("serial")
public class StructuredDynamicAttribute extends DynamicAttribute implements Serializable {

	public StructuredDynamicAttribute(String name, String ldapName,	Class<String> attributeClass) {
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

