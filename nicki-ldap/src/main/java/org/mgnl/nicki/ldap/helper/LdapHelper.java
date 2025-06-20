
package org.mgnl.nicki.ldap.helper;

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


import java.util.Collection;
import java.util.List;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.helper.PathHelper;
import org.mgnl.nicki.core.objects.DataModel;
import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicObject;


/**
 * The Class LdapHelper.
 */
public class LdapHelper extends PathHelper {

	/**
	 * The Enum LOGIC.
	 */
	public enum LOGIC {

		/** The and. */
		AND("&"),
		
		/** The or. */
		OR("|");
		
		/** The sign. */
		private String sign;

		/**
		 * Instantiates a new logic.
		 *
		 * @param sign the sign
		 */
		LOGIC(String sign) {
			this.sign = sign;
		}

		;

		/**
		 * Gets the sign.
		 *
		 * @return the sign
		 */
		public String getSign() {
			return this.sign;
		}
	}

	/**
	 * Gets the path.
	 *
	 * @param parentPath the parent path
	 * @param namingLdapAttribute the naming ldap attribute
	 * @param namingValue the naming value
	 * @return the path
	 */
	public static String getPath(String parentPath, String namingLdapAttribute, String namingValue) {
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtils.upperCase(namingLdapAttribute));
		sb.append("=");
		sb.append(namingValue);
		sb.append(",");
		sb.append(parentPath);

		return sb.toString();
	}

	/**
	 * Gets the parent path.
	 *
	 * @param path the path
	 * @return the parent path
	 */
	public static String getParentPath(String path) {
		return StringUtils.strip(StringUtils.substringAfter(path, ","));
	}

	/**
	 * Gets the naming value.
	 *
	 * @param path the path
	 * @return the naming value
	 */
	public static String getNamingValue(String path) {
		return StringUtils.strip(StringUtils.substringAfter(StringUtils.substringBefore(path, ","), "="));
	}
	
	/**
	 * Contains name.
	 *
	 * @param dns the dns
	 * @param name the name
	 * @return true, if successful
	 */
	public static boolean containsName(Collection<String> dns, String name) {
		if (dns != null) {
			for (String path : dns) {
				String namingValue = getNamingValue(path);
				if (StringUtils.equalsIgnoreCase(name, namingValue)) {
					return true;
				}
			}
		}
		return false;
	}
	


	/**
	 * Negate query.
	 *
	 * @param sb the sb
	 */
	public static void negateQuery(StringBuilder sb) {
		if (sb.length() > 0) {
			sb.insert(0, "!");
			sb.insert(0, "(");
			sb.append(")");
		}
	}

	/**
	 * Adds the query.
	 *
	 * @param sb the sb
	 * @param query the query
	 * @param andOr the and or
	 */
	public static void addQuery(StringBuilder sb, String query, LOGIC andOr) {
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


	
	/**
	 * Gets the ldap attributes for create.
	 *
	 * @param dynamicObject the dynamic object
	 * @return the ldap attributes for create
	 */
	public static Attributes getLdapAttributesForCreate(DynamicObject dynamicObject) {
		Attributes myAttrs = new BasicAttributes(true);
		addBasicLdapAttributes(myAttrs, dynamicObject);
		addLdapAttributes(myAttrs, dynamicObject, false);
		return myAttrs;
	}
	
	/**
	 * Adds the basic ldap attributes.
	 *
	 * @param myAttrs the my attrs
	 * @param dynamicObject the dynamic object
	 */
	// objectClass + naming
	public static void addBasicLdapAttributes(Attributes myAttrs, DynamicObject dynamicObject) {
		Attribute oc = new BasicAttribute("objectClass");
		for (String objectClass : dynamicObject.getModel().getObjectClasses()) {
			oc.add(objectClass);
		}
		for (String objectClass : dynamicObject.getModel().getAdditionalObjectClasses()) {
			oc.add(objectClass);
		}
		myAttrs.put(oc);
		for (DynamicAttribute dynAttribute : dynamicObject.getModel().getMandatoryAttributes()) {
			if (dynAttribute.isNaming()) {
				myAttrs.put(dynAttribute.getExternalName(), dynamicObject.getAttribute(dynAttribute.getName()));
			}			
		}
	}
	
	/**
	 * Adds the ldap attributes.
	 *
	 * @param myAttrs the my attrs
	 * @param dynamicObject the dynamic object
	 * @param nullable the nullable
	 */
	public static void addLdapAttributes(Attributes myAttrs, DynamicObject dynamicObject, boolean nullable) {

		// single attributes (except namingAttribute)
		for (DynamicAttribute dynAttribute : dynamicObject.getModel().getAttributes().values()) {
			if (!dynAttribute.isNaming()&& !dynAttribute.isMultiple() && !dynAttribute.isReadonly()) {
				Object value;
				if (dynAttribute.getType() == String.class) {
					value = StringUtils.trimToNull(dynamicObject.getAttribute(dynAttribute.getName()));
				} else {
					value = dynamicObject.get(dynAttribute.getName());
				}
				if (nullable || value != null) {
					Attribute attribute = new BasicAttribute(dynAttribute.getExternalName(), value);
					myAttrs.put(attribute);
				}
			}
		}
		
		// multi attributes
		for (DynamicAttribute dynAttribute : dynamicObject.getModel().getAttributes().values()) {
			if (dynAttribute.isMultiple() && !dynAttribute.isReadonly()) {
				Attribute attribute = new BasicAttribute(dynAttribute.getExternalName());
				@SuppressWarnings("unchecked")
				List<String> list = (List<String>) dynamicObject.get(dynAttribute.getName());
				if (list != null) {
					for (String value : list) {
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
	

	/**
	 * Gets the object class filter.
	 *
	 * @param model the model
	 * @return the object class filter
	 */
	public static String getObjectClassFilter(DataModel model) {
		StringBuilder sb = new StringBuilder();
		for (String objectClass : model.getObjectClasses()) {
			LdapHelper.addQuery(sb, "objectClass=" + objectClass, LOGIC.AND);
		}

		return sb.toString();
	}


}
