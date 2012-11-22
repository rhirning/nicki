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
package org.mgnl.nicki.ldap.helper;

import java.util.Iterator;
import java.util.List;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.helper.PathHelper;
import org.mgnl.nicki.core.objects.DataModel;
import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.BaseLdapDynamicObject;

public class LdapHelper extends PathHelper {

	public enum LOGIC {

		AND("&"),
		OR("|");
		private String sign;

		LOGIC(String sign) {
			this.sign = sign;
		}

		;

		public String getSign() {
			return this.sign;
		}
	}

	public static String getPath(String parentPath, String namingLdapAttribute, String namingValue) {
		StringBuffer sb = new StringBuffer();
		sb.append(StringUtils.upperCase(namingLdapAttribute));
		sb.append("=");
		sb.append(namingValue);
		sb.append(",");
		sb.append(parentPath);

		return sb.toString();
	}

	public static String getParentPath(String path) {
		return StringUtils.strip(StringUtils.substringAfter(path, ","));
	}

	public static String getNamingValue(String path) {
		return StringUtils.strip(StringUtils.substringAfter(StringUtils.substringBefore(path, ","), "="));
	}

	public static void negateQuery(StringBuffer sb) {
		if (sb.length() > 0) {
			sb.insert(0, "!");
			sb.insert(0, "(");
			sb.append(")");
		}
	}

	public static void addQuery(StringBuffer sb, String query, LOGIC andOr) {
		if (sb.length() == 0) {
			sb.append(query);
			if (!StringUtils.startsWith(query, "(")) {
				sb.insert(0,"(");
				sb.append(")");
			}
		} else {
			sb.insert(0, andOr.getSign());
			sb.insert(0, "(");
			if (!StringUtils.startsWith(query, "(")) {
				sb.append("(");
			}
			sb.append(query);
			if (!StringUtils.startsWith(query, "(")) {
				sb.append(")");
			}
			sb.append(")");
		}
	}


	
	public static Attributes getLdapAttributesForCreate(BaseLdapDynamicObject dynamicObject) {
		Attributes myAttrs = new BasicAttributes(true);
		addBasicLdapAttributes(myAttrs, dynamicObject);
		addLdapAttributes(myAttrs, dynamicObject, false);
		return myAttrs;
	}
	
	// objectClass + naming
	public static void addBasicLdapAttributes(Attributes myAttrs, BaseLdapDynamicObject dynamicObject) {
		Attribute oc = new BasicAttribute("objectClass");
		for (Iterator<String> iterator = dynamicObject.getModel().getObjectClasses().iterator(); iterator.hasNext();) {
			String objectClass = iterator.next();
			oc.add(objectClass);
		}
		for (Iterator<String> iterator = dynamicObject.getModel().getAdditionalObjectClasses().iterator(); iterator.hasNext();) {
			String objectClass = iterator.next();
			oc.add(objectClass);
		}
		myAttrs.put(oc);
		for (Iterator<DynamicAttribute> iterator = dynamicObject.getModel().getMandatoryAttributes().iterator(); iterator.hasNext();) {
			DynamicAttribute dynAttribute =  iterator.next();
			if (dynAttribute.isNaming()) {
				myAttrs.put(dynAttribute.getExternalName(), dynamicObject.getAttribute(dynAttribute.getName()));
			}			
		}
	}
	
	public static void addLdapAttributes(Attributes myAttrs, DynamicObject dynamicObject, boolean nullable) {

		// single attributes (except namingAttribute)
		for (Iterator<DynamicAttribute> iterator = dynamicObject.getModel().getAttributes().values().iterator(); iterator.hasNext();) {
			DynamicAttribute dynAttribute = iterator.next();
			if (!dynAttribute.isNaming()&& !dynAttribute.isMultiple() && !dynAttribute.isReadonly()) {
				String value = StringUtils.trimToNull(dynamicObject.getAttribute(dynAttribute.getName()));
				if (nullable || value != null) {
					Attribute attribute = new BasicAttribute(dynAttribute.getExternalName(), value);
					myAttrs.put(attribute);
				}
			}
		}
		
		// multi attributes
		for (Iterator<DynamicAttribute> iterator = dynamicObject.getModel().getAttributes().values().iterator(); iterator.hasNext();) {
			DynamicAttribute dynAttribute = iterator.next();
			if (dynAttribute.isMultiple() && !dynAttribute.isReadonly()) {
				Attribute attribute = new BasicAttribute(dynAttribute.getExternalName());
				@SuppressWarnings("unchecked")
				List<String> list = (List<String>) dynamicObject.get(dynAttribute.getName());
				if (list != null) {
					for (Iterator<String> iterator2 = list.iterator(); iterator2.hasNext();) {
						String value = iterator2.next();
						if (StringUtils.isNotEmpty(value)) {
							attribute.add(value);
						}
					}
				}
				if (nullable || attribute.size() > 0) {
					myAttrs.put(attribute);
				}
			}
		}
		
	}
	

	public static String getObjectClassFilter(DataModel model) {
		StringBuffer sb = new StringBuffer();
		for (Iterator<String> iterator = model.getObjectClasses().iterator(); iterator.hasNext();) {
			String objectClass = iterator.next();
			LdapHelper.addQuery(sb, "objectClass=" + objectClass, LOGIC.AND);
		}

		return sb.toString();
	}


}
