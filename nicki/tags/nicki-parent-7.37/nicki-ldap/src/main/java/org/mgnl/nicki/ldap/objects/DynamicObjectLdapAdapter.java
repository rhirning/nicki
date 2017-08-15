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

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectAdapter;
import org.mgnl.nicki.ldap.helper.LdapHelper;

@SuppressWarnings("serial")
public class DynamicObjectLdapAdapter implements DynamicObjectAdapter {
		
	public DynamicObjectLdapAdapter() {
	}
	
	@Override
	public void initNew(DynamicObject dynamicObject, String parentPath, String namingValue) {
		dynamicObject.setStatus(DynamicObject.STATUS.NEW);
		dynamicObject.setParentPath(parentPath);
		dynamicObject.setPath(LdapHelper.getPath(parentPath, dynamicObject.getModel().getNamingLdapAttribute(), namingValue));
		dynamicObject.put(dynamicObject.getModel().getNamingAttribute(), namingValue);
	}

	@Override
	public String getParentPath(DynamicObject dynamicObject) {
		return LdapHelper.getParentPath(dynamicObject.getPath());
	}

	@Override
	public void initExisting(DynamicObject dynamicObject, NickiContext context, String path) {
		dynamicObject.setStatus(DynamicObject.STATUS.EXISTS);
		dynamicObject.setContext(context);
		dynamicObject.setPath(path);
		dynamicObject.setParentPath(LdapHelper.getParentPath(dynamicObject.getPath()));

		dynamicObject.getMap().put(dynamicObject.getModel().getNamingAttribute(), LdapHelper.getNamingValue(path));
	}

	@Override
	public boolean accept(DynamicObject dynamicObject, ContextSearchResult rs) {
		boolean accepted = true;
		for (String objectClass : dynamicObject.getModel().getObjectClasses()) {
			accepted &= checkAttribute(rs,"objectClass", objectClass);
		}
		return accepted;
	}

	@Override
	public boolean checkAttribute(ContextSearchResult rs, String attribute,
			String value) {
		try {
			for (Object attributeValue : rs.getValues(attribute)) {
				if (StringUtils.equalsIgnoreCase(value, (String) attributeValue)) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void merge(DynamicObject dynamicObject, Map<DynamicAttribute, Object> changeAttributes) {
		for (DynamicAttribute dynamicAttribute : changeAttributes.keySet()) {
			dynamicObject.put(dynamicAttribute.getName(), changeAttributes.get(dynamicAttribute));
		}
	};

	@Override
	public String getLocalizedValue(DynamicObject dynamicObject, String attributeName, String locale) {
		Map<String, String> valueMap = DataHelper.getMap(dynamicObject.getAttribute(attributeName), "|", "~");
		if (valueMap.size() == 0) {
			return null;
		} else if (valueMap.containsKey(locale)) {
			return valueMap.get(locale);
		} else {
			return valueMap.values().iterator().next();
		}
	}

	@Override
	public String getPath(DynamicObject dynamicObject, String parentPath, String name) {
		return LdapHelper.getPath(parentPath, dynamicObject.getModel().getNamingLdapAttribute(), name);
	}

	@Override
	public String getObjectClassFilter(DynamicObject dynamicObject) {
		return LdapHelper.getObjectClassFilter(dynamicObject.getModel());
	}

}
