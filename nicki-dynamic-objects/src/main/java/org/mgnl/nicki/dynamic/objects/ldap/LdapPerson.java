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
package org.mgnl.nicki.dynamic.objects.ldap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.ldap.objects.DynamicLdapAttribute;
import org.mgnl.nicki.ldap.objects.DynamicLdapTemplateObject;
import org.mgnl.nicki.ldap.objects.DynamicReference;
import org.mgnl.nicki.dynamic.objects.shop.AssignedArticle;


@SuppressWarnings("serial")
@DynamicObject(target="edir")
public class LdapPerson extends DynamicLdapTemplateObject {
	public static final String ATTRIBUTE_SURNAME = "surname";
	public static final String ATTRIBUTE_GIVENNAME = "givenname";
	public static final String ATTRIBUTE_FULLNAME = "fullname";
	public static final String ATTRIBUTE_LANGUAGE = "language";
	public static final String ATTRIBUTE_MEMBEROF = "memberOf";
	public static final String ATTRIBUTE_LOCATION = "location";
	public static final String ATTRIBUTE_ASSIGNEDARTICLE = "assignedArticle";
	public static final String ATTRIBUTE_ATTRIBUTEVALUE = "attributeValue";

	public static final String SEPARATOR_SPECIFIER = "|";
	public static final String SEPARATOR_KEY = "|";
	public static final String SEPARATOR_VALUE = "=";

	private List<AssignedArticle> assignedArticles = null;
	private List<String> attributeValues = null;
	private Map<String, String> catalogAttributes = new HashMap<String, String>();
	
	public void initDataModel() {
		addObjectClass("Person");
		addAdditionalObjectClass("nickiUserAux");
		DynamicLdapAttribute dynAttribute = new DynamicLdapAttribute(ATTRIBUTE_NAME, "cn",
				String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicLdapAttribute(ATTRIBUTE_SURNAME, "sn", String.class);
		dynAttribute.setMandatory();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicLdapAttribute(ATTRIBUTE_GIVENNAME, "givenName",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicLdapAttribute(ATTRIBUTE_FULLNAME, "fullName",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicLdapAttribute(ATTRIBUTE_LANGUAGE, "Language",
				String.class);
		addAttribute(dynAttribute);

		
		dynAttribute = new DynamicReference(LdapGroup.class, ATTRIBUTE_MEMBEROF, Config.getProperty("nicki.data.basedn"), 
				"member", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicLdapAttribute(ATTRIBUTE_LOCATION, "nickiLocation",
				String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicLdapAttribute(ATTRIBUTE_ASSIGNEDARTICLE, "nickiCatalogArticle",
				String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicLdapAttribute(ATTRIBUTE_ATTRIBUTEVALUE,
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

	public void setLanguage(String value) {
		put(ATTRIBUTE_LANGUAGE, value);
	}

	public String getLanguage() {
		return (String) get(ATTRIBUTE_LANGUAGE);
	}

	@SuppressWarnings("unchecked")
	public List<String> getCatalogAttributeValues() {
		if (this.attributeValues == null) {
			if (get("attributeValue") != null) {
				this.attributeValues = (List<String>) get(ATTRIBUTE_ATTRIBUTEVALUE);
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
		StringBuffer prefix = new StringBuffer(articleId);
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

	public List<AssignedArticle> getAssignedArticles() {
		if (this.assignedArticles == null) {
			this.assignedArticles = new ArrayList<AssignedArticle>();
			@SuppressWarnings("unchecked")
			List<String> articles = (List<String>) get(ATTRIBUTE_ASSIGNEDARTICLE);
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
}
