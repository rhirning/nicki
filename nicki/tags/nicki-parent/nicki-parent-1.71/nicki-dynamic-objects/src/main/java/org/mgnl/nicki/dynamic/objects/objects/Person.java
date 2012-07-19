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
package org.mgnl.nicki.dynamic.objects.objects;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicReference;

@SuppressWarnings("serial")
public class Person extends DynamicTemplateObject {
	public static final String ATTRIBUTE_SURNAME = "surname";
	public static final String ATTRIBUTE_GIVENNAME = "givenname";
	public static final String ATTRIBUTE_FULLNAME = "fullname";
	public static final String ATTRIBUTE_LANGUAGE = "language";
	public static final String ATTRIBUTE_MEMBEROF = "memberOf";
	public static final String ATTRIBUTE_STATUS = "status";
	public static final String ATTRIBUTE_LOCATION = "location";
	public static final String ATTRIBUTE_ASSIGNEDARTICLE = "assignedArticle";
	public static final String ATTRIBUTE_ATTRIBUTEVALUE = "attributeValue";

	public void initDataModel() {
		addObjectClass("Person");
		addAdditionalObjectClass("nickiUserAux");
		DynamicAttribute dynAttribute = new DynamicAttribute(ATTRIBUTE_NAME, "cn",
				String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_SURNAME, "sn", String.class);
		dynAttribute.setMandatory();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_GIVENNAME, "givenName",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_FULLNAME, "fullName",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_LANGUAGE, "Language",
				String.class);
		addAttribute(dynAttribute);

		
		dynAttribute = new DynamicReference(Group.class, ATTRIBUTE_MEMBEROF, Config.getProperty("nicki.data.basedn"), 
				"member", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_STATUS, "nickiStatus",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_LOCATION, "nickiLocation",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_ASSIGNEDARTICLE, "nickiCatalogArticle",
				String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute(ATTRIBUTE_ATTRIBUTEVALUE,
				"nickiCatalogAttribute", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);

	}

	@Override
	public String getDisplayName() {
		StringBuffer sb = new StringBuffer();
		sb.append(StringUtils.trimToEmpty(getAttribute(ATTRIBUTE_GIVENNAME)));
		if (sb.length() > 0) {
			sb.append(" ");
		}
		sb.append(StringUtils.trimToEmpty(getAttribute(ATTRIBUTE_SURNAME)));
		sb.append(" (");
		sb.append(getName());
		sb.append(")");
		return sb.toString();
	}


	public String getFullname() {
		return getAttribute(ATTRIBUTE_FULLNAME);
	}

	public void setName(String value) {
		put(ATTRIBUTE_SURNAME, value);
	}

	public void setGivenName(String value) {
		put(ATTRIBUTE_GIVENNAME, value);
	}

	public void setLanguage(String value) {
		put(ATTRIBUTE_LANGUAGE, value);
	}

	public String getLanguage() {
		return (String) get(ATTRIBUTE_LANGUAGE);
	}

	public static String getActiveFilter() {
		return "nickiStatus=" + STATUS.ACTIVE.getValue();
	}

	public static String getInActiveFilter() {
		return "!(|(nickiStatus=" + STATUS.ACTIVE.getValue() + ")(nickiStatus="
				+ STATUS.REQUESTED.getValue() + "))";
	}

	public static String getAllFilter() {
		return "!(nickiStatus=" + STATUS.REQUESTED.getValue() + ")";
	}

	public void setStatus(STATUS status) {
		if(null != status) {
			put(ATTRIBUTE_STATUS, status.getValue());
		} else {
			clear(ATTRIBUTE_STATUS);
		}
		
	}

	public STATUS getStatus() {
		return STATUS.fromValue(getAttribute(ATTRIBUTE_STATUS));
	}

	public enum STATUS {

		REQUESTED("beantragt"), INACTIVE("inaktiv-vor-eintritt"), ACTIVE(
		"aktiv"), DEACTIVATED("deaktiviert"), RESIGNED("ausgetreten"), NOT_SET(
		"");
		private final String status;

		private STATUS(String status) {
			this.status = status;
		}

		public static STATUS fromValue(String status) {
			if (INACTIVE.getValue().equals(status)) {
				return INACTIVE;
			} else if (ACTIVE.getValue().equals(status)) {
				return ACTIVE;
			} else if (DEACTIVATED.getValue().equals(status)) {
				return DEACTIVATED;
			} else if (RESIGNED.getValue().equals(status)) {
				return RESIGNED;
			} else if (REQUESTED.getValue().equals(status)) {
				return REQUESTED;
			}

			return NOT_SET;

		}

		public String getValue() {
			return status;
		}
	}


}
