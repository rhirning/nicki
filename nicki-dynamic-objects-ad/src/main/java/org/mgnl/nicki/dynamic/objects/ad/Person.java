
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


/**
 * The Class Person.
 */
@SuppressWarnings("serial")
@DynamicObject
@ObjectClass("Person")
@AdditionalObjectClass({"organizationalPerson", "user"})
public class Person extends BaseDynamicObject {
	
	/** The Constant ATTRIBUTE_DISPLAYNAME. */
	public static final String ATTRIBUTE_DISPLAYNAME = "displayName";
	
	/** The Constant ATTRIBUTE_SURNAME. */
	public static final String ATTRIBUTE_SURNAME = "surname";
	
	/** The Constant ATTRIBUTE_GIVENNAME. */
	public static final String ATTRIBUTE_GIVENNAME = "givenname";
	
	/** The Constant ATTRIBUTE_INSTANCE_TYPE. */
	public static final String ATTRIBUTE_INSTANCE_TYPE = "instanceType";
	
	/** The Constant ATTRIBUTE_OBJECT_CATEGORY. */
	public static final String ATTRIBUTE_OBJECT_CATEGORY = "objectCategory";
	
	/** The Constant ATTRIBUTE_FULLNAME. */
	public static final String ATTRIBUTE_FULLNAME = "fullname";
	
	/** The Constant ATTRIBUTE_LANGUAGE. */
	public static final String ATTRIBUTE_LANGUAGE = "language";
	
	/** The Constant ATTRIBUTE_MEMBEROF. */
	public static final String ATTRIBUTE_MEMBEROF = "memberOf";
	
	/** The Constant ATTRIBUTE_LOCATION. */
	public static final String ATTRIBUTE_LOCATION = "location";
	
	/** The Constant ATTRIBUTE_ACCOUNT_NAME. */
	public static final String ATTRIBUTE_ACCOUNT_NAME = "accountname";
	
	/** The Constant ATTRIBUTE_PRINCIPAL_NAME. */
	public static final String ATTRIBUTE_PRINCIPAL_NAME = "principalname";
	
	/** The Constant ATTRIBUTE_ASSIGNEDARTICLE. */
	public static final String ATTRIBUTE_ASSIGNEDARTICLE = "assignedArticle";
	
	/** The Constant ATTRIBUTE_ATTRIBUTEVALUE. */
	public static final String ATTRIBUTE_ATTRIBUTEVALUE = "attributeValue";
	
	/** The Constant ATTRIBUTE_IS_MANAGER. */
	public static final String ATTRIBUTE_IS_MANAGER = "isManager";

	/** The name. */
	@DynamicAttribute(externalName="cn", naming=true)
	private String name;	
	
	/** The surname. */
	@DynamicAttribute(externalName="sn", mandatory=true)
	private String surname;
	
	/** The givenname. */
	@DynamicAttribute(externalName="givenName")
	private String givenname;
	
	/** The instance type. */
	@DynamicAttribute(externalName="instanceType")
	private String instanceType;
	
	/** The object category. */
	@DynamicAttribute(externalName="objectCategory")
	private String objectCategory;
	
	/** The fullname. */
	@DynamicAttribute(externalName="displayName")
	private String fullname;
	
	/** The accountname. */
	@DynamicAttribute(externalName="sAMAccountName")
	private String accountname;
	
	/** The principalname. */
	@DynamicAttribute(externalName="userPrincipalName")
	private String principalname;
	
	/** The group. */
	@DynamicAttribute(externalName="member", foreignKey=Group.class)
	private String[] group;
	
	/**
	 * Gets the fullname.
	 *
	 * @return the fullname
	 */
	public String getFullname() {
		return getAttribute("fullname");
	}

	/**
	 * Sets the surname.
	 *
	 * @param value the new surname
	 */
	public void setSurname(String value) {
		put(ATTRIBUTE_SURNAME, value);
	}

	/**
	 * Gets the surname.
	 *
	 * @return the surname
	 */
	public String getSurname() {
		return getAttribute(ATTRIBUTE_SURNAME);
	}

	/**
	 * Gets the givenname.
	 *
	 * @return the givenname
	 */
	public String getGivenname() {
		return getAttribute(ATTRIBUTE_GIVENNAME);
	}

	/**
	 * Sets the given name.
	 *
	 * @param value the new given name
	 */
	public void setGivenName(String value) {
		put(ATTRIBUTE_GIVENNAME, value);
	}

	/**
	 * Gets the instance type.
	 *
	 * @return the instance type
	 */
	public String getInstanceType() {
		return getAttribute(ATTRIBUTE_INSTANCE_TYPE);
	}

	/**
	 * Sets the instance type.
	 *
	 * @param value the new instance type
	 */
	public void setInstanceType(String value) {
		put(ATTRIBUTE_INSTANCE_TYPE, value);
	}

	/**
	 * Gets the object category.
	 *
	 * @return the object category
	 */
	public String getObjectCategory() {
		return getAttribute(ATTRIBUTE_OBJECT_CATEGORY);
	}

	/**
	 * Sets the object category.
	 *
	 * @param value the new object category
	 */
	public void setObjectCategory(String value) {
		put(ATTRIBUTE_OBJECT_CATEGORY, value);
	}

	/**
	 * Gets the account name.
	 *
	 * @return the account name
	 */
	public String getAccountName() {
		return getAttribute(ATTRIBUTE_ACCOUNT_NAME);
	}

	/**
	 * Sets the account name.
	 *
	 * @param value the new account name
	 */
	public void setAccountName(String value) {
		put(ATTRIBUTE_ACCOUNT_NAME, value);
	}

	/**
	 * Gets the principal name.
	 *
	 * @return the principal name
	 */
	public String getPrincipalName() {
		return getAttribute(ATTRIBUTE_PRINCIPAL_NAME);
	}

	/**
	 * Sets the principal name.
	 *
	 * @param value the new principal name
	 */
	public void setPrincipalName(String value) {
		put(ATTRIBUTE_PRINCIPAL_NAME, value);
	}
}
