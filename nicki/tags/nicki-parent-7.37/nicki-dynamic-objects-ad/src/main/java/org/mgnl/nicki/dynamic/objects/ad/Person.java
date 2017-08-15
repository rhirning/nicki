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
package org.mgnl.nicki.dynamic.objects.ad;

import org.mgnl.nicki.core.annotation.AdditionalObjectClass;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.objects.BaseDynamicObject;

@SuppressWarnings("serial")
@DynamicObject
@ObjectClass("Person")
@AdditionalObjectClass({"organizationalPerson", "user"})
public class Person extends BaseDynamicObject {
	public static final String ATTRIBUTE_DISPLAYNAME = "displayName";
	public static final String ATTRIBUTE_SURNAME = "surname";
	public static final String ATTRIBUTE_GIVENNAME = "givenname";
	public static final String ATTRIBUTE_INSTANCE_TYPE = "instanceType";
	public static final String ATTRIBUTE_OBJECT_CATEGORY = "objectCategory";
	public static final String ATTRIBUTE_FULLNAME = "fullname";
	public static final String ATTRIBUTE_LANGUAGE = "language";
	public static final String ATTRIBUTE_MEMBEROF = "memberOf";
	public static final String ATTRIBUTE_LOCATION = "location";
	public static final String ATTRIBUTE_ACCOUNT_NAME = "accountname";
	public static final String ATTRIBUTE_PRINCIPAL_NAME = "principalname";
	public static final String ATTRIBUTE_ASSIGNEDARTICLE = "assignedArticle";
	public static final String ATTRIBUTE_ATTRIBUTEVALUE = "attributeValue";
	public static final String ATTRIBUTE_IS_MANAGER = "isManager";

	@DynamicAttribute(externalName="cn", naming=true)
	private String name;	
	@DynamicAttribute(externalName="sn", mandatory=true)
	private String surname;
	@DynamicAttribute(externalName="givenName")
	private String givenname;
	@DynamicAttribute(externalName="instanceType")
	private String instanceType;
	@DynamicAttribute(externalName="objectCategory")
	private String objectCategory;
	@DynamicAttribute(externalName="displayName")
	private String fullname;
	@DynamicAttribute(externalName="sAMAccountName")
	private String accountname;
	@DynamicAttribute(externalName="userPrincipalName")
	private String principalname;
	@DynamicAttribute(externalName="member", foreignKey=Group.class)
	private String[] group;
	
	public String getFullname() {
		return getAttribute("fullname");
	}

	public void setSurname(String value) {
		put(ATTRIBUTE_SURNAME, value);
	}

	public String getSurname() {
		return getAttribute(ATTRIBUTE_SURNAME);
	}

	public String getGivenname() {
		return getAttribute(ATTRIBUTE_GIVENNAME);
	}

	public void setGivenName(String value) {
		put(ATTRIBUTE_GIVENNAME, value);
	}

	public String getInstanceType() {
		return getAttribute(ATTRIBUTE_INSTANCE_TYPE);
	}

	public void setInstanceType(String value) {
		put(ATTRIBUTE_INSTANCE_TYPE, value);
	}

	public String getObjectCategory() {
		return getAttribute(ATTRIBUTE_OBJECT_CATEGORY);
	}

	public void setObjectCategory(String value) {
		put(ATTRIBUTE_OBJECT_CATEGORY, value);
	}

	public String getAccountName() {
		return getAttribute(ATTRIBUTE_ACCOUNT_NAME);
	}

	public void setAccountName(String value) {
		put(ATTRIBUTE_ACCOUNT_NAME, value);
	}

	public String getPrincipalName() {
		return getAttribute(ATTRIBUTE_PRINCIPAL_NAME);
	}

	public void setPrincipalName(String value) {
		put(ATTRIBUTE_PRINCIPAL_NAME, value);
	}
}
