/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.dynamic.objects.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.annotation.AdditionalObjectClass;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.DynamicReferenceAttribute;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.objects.BaseDynamicObject;
import org.mgnl.nicki.dynamic.objects.shop.AssignedArticle;
@SuppressWarnings("serial")
@DynamicObject
@ObjectClass("Person")
@AdditionalObjectClass("nickiUserAux")
public class Person extends BaseDynamicObject {
	public static final String ATTRIBUTE_DISPLAYNAME = "displayName";
	public static final String ATTRIBUTE_SURNAME = "surname";
	public static final String ATTRIBUTE_GIVENNAME = "givenName";
	public static final String ATTRIBUTE_FULLNAME = "fullname";
	public static final String ATTRIBUTE_LANGUAGE = "language";
	public static final String ATTRIBUTE_MEMBER = "member";
	public static final String ATTRIBUTE_LOCATION = "location";
	public static final String ATTRIBUTE_ASSIGNEDARTICLE = "assignedArticle";
	public static final String ATTRIBUTE_ATTRIBUTEVALUE = "attributeValue";
	public static final String ATTRIBUTE_IS_MANAGER = "isManager";

	public static final String SEPARATOR_SPECIFIER = "|";
	public static final String SEPARATOR_KEY = "|";
	public static final String SEPARATOR_VALUE = "=";

	private Collection<AssignedArticle> assignedArticles;
	private Collection<Group> assignedGroups;
	private Collection<String> attributeValues;
	private Map<String, String> catalogAttributes = new HashMap<String, String>();

	@DynamicAttribute(externalName="isManager")
	private boolean isManager;

	@DynamicReferenceAttribute(externalName="member", reference=Group.class,
			baseProperty="nicki.data.basedn")
	private String[] member;
	@DynamicAttribute(externalName="nickiLocation")
	private String location;
	@DynamicAttribute(externalName="nickiCatalogArticle")
	private String[] assignedArticle;
	@DynamicAttribute(externalName="nickiCatalogAttribute")
	private String[] attributeValue;

	@Override
	@DynamicAttribute(externalName="cn", naming=true)
	public String getName() {
		return super.getName();
	}

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


	public Collection<Group> getGroups() {
		if (assignedGroups == null) {
			assignedGroups = getForeignKeyObjects(Group.class, "memberOf");
		}
		return assignedGroups;
	}

	@DynamicAttribute(externalName="fullName")
	public String getFullName() {
		return getAttribute(ATTRIBUTE_FULLNAME);
	}
	public String getFullname() {
		return getFullName();
	}

	public void setSurname(String value) {
		put(ATTRIBUTE_SURNAME, value);
	}

	@DynamicAttribute(externalName="sn", mandatory=true)
	public String getSurname() {
		return getAttribute(ATTRIBUTE_SURNAME);
	}

	@DynamicAttribute(externalName="givenName")
	public String getGivenName() {
		return getAttribute(ATTRIBUTE_GIVENNAME);
	}
	public String getGivenname() {
		return getGivenName();
	}

	public void setGivenName(String value) {
		put(ATTRIBUTE_GIVENNAME, value);
	}

	public void setLanguage(String value) {
		put(ATTRIBUTE_LANGUAGE, value);
	}

	@DynamicAttribute(externalName="Language")
	public String getLanguage() {
		return (String) get(ATTRIBUTE_LANGUAGE);
	}

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

	public Map<String, String> getCatalogAttributes() {
		return catalogAttributes;
	}

	public Map<String, String> getCatalogAttributes(AssignedArticle assignedArticle) {
		return getCatalogAttributes(assignedArticle.getArticleId(), assignedArticle.getSpecifier());
	}

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

	public String getActiveFilter() {
		return "!(nickiStatus=inactive)";
	}


	public boolean hasGroup(String groupName) {
		return false;
	}


	public boolean isMemberOf(String group) {
		return false;
	}


	public boolean hasRole(String role) {
		return false;
	}

	public boolean hasManagerFlag() {
		return DataHelper.booleanOf(getAttribute(ATTRIBUTE_IS_MANAGER));
	}

	public void setManagerFlag(boolean value) {
		put(ATTRIBUTE_IS_MANAGER, value?"TRUE":"FALSE");
	}
}
