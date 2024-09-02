
package org.mgnl.nicki.ldap.objects;

/*-
 * #%L
 * nicki-ldap
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


import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
