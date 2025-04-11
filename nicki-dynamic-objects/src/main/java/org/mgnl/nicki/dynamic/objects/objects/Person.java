
package org.mgnl.nicki.dynamic.objects.objects;

/*-
 * #%L
 * nicki-dynamic-objects
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


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.annotation.AdditionalObjectClass;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.DynamicReferenceAttribute;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.objects.BaseDynamicObject;
import org.mgnl.nicki.dynamic.objects.shop.AssignedArticle;
// TODO: Auto-generated Javadoc

/**
 * The Class Person.
 */
@SuppressWarnings("serial")
@DynamicObject
@ObjectClass("Person")
@AdditionalObjectClass("nickiUserAux")
public class Person extends BaseDynamicObject {
	
	/** The Constant ATTRIBUTE_DISPLAYNAME. */
	public static final String ATTRIBUTE_DISPLAYNAME = "displayName";
	
	/** The Constant ATTRIBUTE_SURNAME. */
	public static final String ATTRIBUTE_SURNAME = "surname";
	
	/** The Constant ATTRIBUTE_GIVENNAME. */
	public static final String ATTRIBUTE_GIVENNAME = "givenName";
	
	/** The Constant ATTRIBUTE_FULLNAME. */
	public static final String ATTRIBUTE_FULLNAME = "fullname";
	
	/** The Constant ATTRIBUTE_LANGUAGE. */
	public static final String ATTRIBUTE_LANGUAGE = "language";
	
	/** The Constant ATTRIBUTE_MEMBER. */
	public static final String ATTRIBUTE_MEMBER = "member";
	
	/** The Constant ATTRIBUTE_LOCATION. */
	public static final String ATTRIBUTE_LOCATION = "location";
	
	/** The Constant ATTRIBUTE_ASSIGNEDARTICLE. */
	public static final String ATTRIBUTE_ASSIGNEDARTICLE = "assignedArticle";
	
	/** The Constant ATTRIBUTE_ATTRIBUTEVALUE. */
	public static final String ATTRIBUTE_ATTRIBUTEVALUE = "attributeValue";
	
	/** The Constant ATTRIBUTE_IS_MANAGER. */
	public static final String ATTRIBUTE_IS_MANAGER = "isManager";

	/** The Constant SEPARATOR_SPECIFIER. */
	public static final String SEPARATOR_SPECIFIER = "|";
	
	/** The Constant SEPARATOR_KEY. */
	public static final String SEPARATOR_KEY = "|";
	
	/** The Constant SEPARATOR_VALUE. */
	public static final String SEPARATOR_VALUE = "=";

	/** The assigned articles. */
	private Collection<AssignedArticle> assignedArticles;
	
	/** The assigned groups. */
	private Collection<Group> assignedGroups;
	
	/** The attribute values. */
	private Collection<String> attributeValues;
	
	/** The catalog attributes. */
	private Map<String, String> catalogAttributes = new HashMap<String, String>();
	
	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	@DynamicAttribute(externalName="nickiLocation")
	public String getLocation() {
		return (String) this.get(ATTRIBUTE_LOCATION);
	}
	
	/**
	 * Gets the assigned article.
	 *
	 * @return the assigned article
	 */
	@SuppressWarnings("unchecked")
	@DynamicAttribute(externalName="nickiCatalogArticle")
	private List<String> getAssignedArticle() {
		return (List<String>) this.get(ATTRIBUTE_ASSIGNEDARTICLE);
	}
	
	
	/**
	 * Gets the attribute value.
	 *
	 * @return the attribute value
	 */
	@SuppressWarnings("unchecked")
	@DynamicAttribute(externalName="nickiCatalogAttribute")
	private List<String> getAttributeValue() {
		return (List<String>) this.get(ATTRIBUTE_ATTRIBUTEVALUE);
	}


	/**
	 * Gets the member.
	 *
	 * @return the member
	 */
	@SuppressWarnings("unchecked")
	@DynamicReferenceAttribute(externalName="member", reference=Group.class,
			baseProperty="nicki.data.basedn")
	public List<String> getMember() {
		return (List<String>) this.get(ATTRIBUTE_MEMBER);
	}

	/**
	 * Checks if is manager.
	 *
	 * @return the boolean
	 */
	public Boolean isManager() {
		if (StringUtils.isNotBlank(getAttribute(ATTRIBUTE_IS_MANAGER))) {
			return DataHelper.booleanOf(getAttribute(ATTRIBUTE_IS_MANAGER));
		} else {
			return false;
		}
	}

	/**
	 * Gets the checks if is manager.
	 *
	 * @return the checks if is manager
	 */
	@DynamicAttribute(externalName="isManager")
	public String getIsManager() {
		return (String) this.get(ATTRIBUTE_IS_MANAGER);
	}

	/**
	 * Sets the checks if is manager.
	 *
	 * @param value the new checks if is manager
	 */
	public void setIsManager(String value) {
		if (StringUtils.isNotBlank(value)) {
			this.put(ATTRIBUTE_IS_MANAGER, value);
		} else {
			this.clear(ATTRIBUTE_IS_MANAGER);
		}
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	@DynamicAttribute(externalName="cn", naming=true)
	public String getName() {
		return super.getName();
	}

	/**
	 * Gets the display name.
	 *
	 * @return the display name
	 */
	@Override
	public String getDisplayName() {
		StringBuilder sb = new StringBuilder();
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


	/**
	 * Gets the groups.
	 *
	 * @return the groups
	 */
	public Collection<Group> getGroups() {
		if (assignedGroups == null) {
			assignedGroups = getForeignKeyObjects(Group.class, "memberOf");
		}
		return assignedGroups;
	}

	/**
	 * Gets the full name.
	 *
	 * @return the full name
	 */
	@DynamicAttribute(externalName="fullName")
	public String getFullName() {
		return getAttribute(ATTRIBUTE_FULLNAME);
	}
	
	/**
	 * Gets the fullname.
	 *
	 * @return the fullname
	 */
	public String getFullname() {
		return getFullName();
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
	@DynamicAttribute(externalName="sn", mandatory=true)
	public String getSurname() {
		return getAttribute(ATTRIBUTE_SURNAME);
	}

	/**
	 * Gets the given name.
	 *
	 * @return the given name
	 */
	@DynamicAttribute(externalName="givenName")
	public String getGivenName() {
		return getAttribute(ATTRIBUTE_GIVENNAME);
	}
	
	/**
	 * Gets the givenname.
	 *
	 * @return the givenname
	 */
	public String getGivenname() {
		return getGivenName();
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
	 * Sets the language.
	 *
	 * @param value the new language
	 */
	public void setLanguage(String value) {
		put(ATTRIBUTE_LANGUAGE, value);
	}

	/**
	 * Gets the language.
	 *
	 * @return the language
	 */
	@DynamicAttribute(externalName="Language")
	public String getLanguage() {
		return (String) get(ATTRIBUTE_LANGUAGE);
	}

	/**
	 * Gets the catalog attribute values.
	 *
	 * @return the catalog attribute values
	 */
	@SuppressWarnings("unchecked")
	public Collection<String> getCatalogAttributeValues() {
		if (this.attributeValues == null) {
			if (get("attributeValue") != null) {
				this.attributeValues = (Collection<String>) get(ATTRIBUTE_ATTRIBUTEVALUE);
			} else {
				this.attributeValues = new ArrayList<String>();
			}
			for (String value : this.attributeValues) {
				String key = StringUtils.substringBefore(value, SEPARATOR_VALUE);
				String attribute = StringUtils.substringAfter(value, SEPARATOR_VALUE);
				this.catalogAttributes.put(key, attribute);
			}
		}
		return this.attributeValues;
	}

	/**
	 * Gets the catalog attributes.
	 *
	 * @return the catalog attributes
	 */
	public Map<String, String> getCatalogAttributes() {
		return catalogAttributes;
	}

	/**
	 * Gets the catalog attributes.
	 *
	 * @param assignedArticle the assigned article
	 * @return the catalog attributes
	 */
	public Map<String, String> getCatalogAttributes(AssignedArticle assignedArticle) {
		return getCatalogAttributes(assignedArticle.getArticleId(), assignedArticle.getSpecifier());
	}

	/**
	 * Gets the catalog attributes.
	 *
	 * @param articleId the article id
	 * @param specifier the specifier
	 * @return the catalog attributes
	 */
	public Map<String, String> getCatalogAttributes(String articleId, String specifier) {
		Map<String,String> result = new HashMap<String, String>();
		StringBuilder prefix = new StringBuilder(articleId);
		if (StringUtils.isNotEmpty(specifier)) {
			prefix.append(SEPARATOR_SPECIFIER).append(specifier);
		}
		prefix.append(SEPARATOR_KEY);

		for (String value : getCatalogAttributeValues()) {
			if (StringUtils.startsWith(value, prefix.toString())) {
				String name = StringUtils.substringBefore(value, SEPARATOR_VALUE);
				String key = StringUtils.substringAfterLast(name, SEPARATOR_KEY);
				String attribute = StringUtils.substringAfter(value, SEPARATOR_VALUE);
				result.put(key, attribute);
			}
		}
		return result;
	}

	/**
	 * Gets the assigned articles.
	 *
	 * @return the assigned articles
	 */
	public Collection<AssignedArticle> getAssignedArticles() {
		if (this.assignedArticles == null) {
			this.assignedArticles = new ArrayList<AssignedArticle>();
			@SuppressWarnings("unchecked")
			Collection<String> articles = (Collection<String>) get(ATTRIBUTE_ASSIGNEDARTICLE);
			if (articles != null) {
				for (String text : articles) {
					this.assignedArticles.add(new AssignedArticle(text));
				}
			}
		}
		return this.assignedArticles;
	}

	/**
	 * Gets the active filter.
	 *
	 * @return the active filter
	 */
	public String getActiveFilter() {
		return "!(nickiStatus=inactive)";
	}


	/**
	 * Checks for group.
	 *
	 * @param groupName the group name
	 * @return true, if successful
	 */
	public boolean hasGroup(String groupName) {
		return false;
	}


	/**
	 * Checks if is member of.
	 *
	 * @param group the group
	 * @return true, if is member of
	 */
	public boolean isMemberOf(String group) {
		return false;
	}


	/**
	 * Checks for role.
	 *
	 * @param role the role
	 * @return true, if successful
	 */
	public boolean hasRole(String role) {
		return false;
	}

	/**
	 * Checks for manager flag.
	 *
	 * @return true, if successful
	 */
	public boolean hasManagerFlag() {
		return DataHelper.booleanOf(getAttribute(ATTRIBUTE_IS_MANAGER));
	}

	/**
	 * Sets the manager flag.
	 *
	 * @param value the new manager flag
	 */
	public void setManagerFlag(boolean value) {
		put(ATTRIBUTE_IS_MANAGER, value?"TRUE":"FALSE");
	}
}
