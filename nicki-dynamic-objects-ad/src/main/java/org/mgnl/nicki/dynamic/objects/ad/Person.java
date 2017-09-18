
package org.mgnl.nicki.dynamic.objects.ad;

/*-
 * #%L
 * nicki-dynamic-objects-ad
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
