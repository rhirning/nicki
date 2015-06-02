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
package org.mgnl.nicki.core.objects;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.methods.ForeignKeyMethod;
import org.mgnl.nicki.core.methods.ListForeignKeyMethod;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.context.NickiContext;

@SuppressWarnings("serial")
public class DynamicReference extends DynamicAttribute implements Serializable {

	private String baseDn;
	private Class<? extends DynamicObject> classDefinition;
	public DynamicReference(Class<? extends DynamicObject> classDefinition, String name, String baseDn, String externalName, Class<?> attributeClass) {
		super(name, externalName, attributeClass);
		this.classDefinition = classDefinition;
		setVirtual();
		this.setBaseDn(baseDn);
	}
	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}
	public String getBaseDn() {
		return baseDn;
	}
	@Override
	public void init(NickiContext context, DynamicObject dynamicObject, ContextSearchResult rs) {
		if (isMultiple()) {
			List<Object> values = rs.getValues(getExternalName());
			dynamicObject.put(getName(), values);
			dynamicObject.put(getMultipleGetter(getName()),
					new ListForeignKeyMethod(context, rs, getExternalName(), getForeignKeyClass()));

		} else {
			String value = (String) rs.getValue(getType(), getExternalName());
			if (StringUtils.isNotEmpty(value)) {
				dynamicObject.put(getName(), value);
				dynamicObject.put(getGetter(getName()),
						new ForeignKeyMethod(context, rs, getExternalName(), getForeignKeyClass()));
			}
		}
	}

	public Class<? extends DynamicObject> getClassDefinition() {
		return classDefinition;
	}
}

