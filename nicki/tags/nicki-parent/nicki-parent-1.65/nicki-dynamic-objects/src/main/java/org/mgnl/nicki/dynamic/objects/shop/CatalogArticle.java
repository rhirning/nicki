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
package org.mgnl.nicki.dynamic.objects.shop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.mgnl.nicki.core.helper.XMLHelper;
import org.mgnl.nicki.dynamic.objects.objects.DynamicTemplateObject;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.types.TextArea;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;

@SuppressWarnings("serial")
public abstract class CatalogArticle extends DynamicTemplateObject {

	public static final String TYPE_ARTICLE = "ARTICLE";
	
	public static Map<String, CatalogArticleAttribute> fixedAttributes = new HashMap<String, CatalogArticleAttribute>();
	static {
		fixedAttributes.put("dateFrom", new CatalogArticleAttribute("dateFrom",
				"nicki.rights.attribute.dateFrom.label",
				"date"));
		fixedAttributes.put("dateTo", new CatalogArticleAttribute("dateTo",
				"nicki.rights.attribute.dateTo.label",
				"date"));
	}

	public void initDataModel() {
		addObjectClass("nickiCatalogArticle");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("displayName", "displayName", String.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("description", "nickiDescription", TextArea.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("approval", "nickiApproval", String.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("category", "nickiCategory", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("rule", "nickiRule", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setEditorClass("org.mgnl.nicki.shop.rules.RuleAttributeField");
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("attributes", "nickiAttributes", TextArea.class);
		addAttribute(dynAttribute);
	};

	// TODO: class must know how to get the assigned articles
	
	public abstract List<CatalogArticle> getArticles(Person person);
	
	public boolean hasArticle(Person person, CatalogArticle article) {
		return getArticles(person).contains(article);
	}
	
	public String getArticleType() {
		return TYPE_ARTICLE;
	}
	
	public String getArticlePath() {
		return this.getPath();
	}
	
	public void setAttributes(List<CatalogArticleAttribute> attributes) {
		put("attribute", attributes);
	}

	public static Collection<CatalogArticleAttribute> getFixedAttributes() {
		return fixedAttributes.values();
	}

	public Map<String, CatalogArticleAttribute> getAttributeMap() {
		Map<String, CatalogArticleAttribute> map = new HashMap<String, CatalogArticleAttribute>();
		String attributes = getAttribute("attributes");
		if (StringUtils.isNotEmpty(attributes)) {
			try {
				Document document = XMLHelper.documentFromString(attributes);
				@SuppressWarnings("unchecked")
				List<Element> attrs = document.getRootElement().getChildren("attribute");
				if (attrs != null) {
					for (Iterator<Element> iterator = attrs.iterator(); iterator.hasNext();) {
						Element attributeElement = iterator.next();
						map.put(attributeElement.getAttributeValue("name"), new CatalogArticleAttribute(attributeElement));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}
	
	public List<CatalogArticleAttribute> getAttributes() {
		List<CatalogArticleAttribute> list = new ArrayList<CatalogArticleAttribute>();
		String attributes = getAttribute("attributes");
		if (StringUtils.isNotEmpty(attributes)) {
			try {
				Document document = XMLHelper.documentFromString(attributes);
				@SuppressWarnings("unchecked")
				List<Element> attrs = document.getRootElement().getChildren("attribute");
				if (attrs != null) {
					for (Iterator<Element> iterator = attrs.iterator(); iterator.hasNext();) {
						Element attributeElement = iterator.next();
						list.add(new CatalogArticleAttribute(attributeElement));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public List<CatalogArticleAttribute> getInheritedAttributes() {
		try {
			return getParent(CatalogPage.class).getAllAttributes();
		} catch (Exception e) {
			return new ArrayList<CatalogArticleAttribute>();
		}
	}
	
	public List<CatalogArticleAttribute> getAllAttributes() {
		List<CatalogArticleAttribute> list = getAttributes();
		list.addAll(getInheritedAttributes());
		return list;
	}

	public boolean hasAttributes() {
		return getAllAttributes().size() > 0;
	}


	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[catalogArticle name='").append(getName());
		sb.append("' displayName='").append(getDisplayName());
		sb.append("']\n");
		if (hasAttributes()) {
			for (Iterator<CatalogArticleAttribute> iterator = getAllAttributes().iterator(); iterator.hasNext();) {
				sb.append(iterator.next().toString());
			}
		}
		return sb.toString();
	}

	public static CatalogArticleAttribute getFixedAttribute(String name) {
		return fixedAttributes.get(name);
	}

	public String getApprovalPath() {
		return getAttribute("approval");
	}
	
	public String getCatalogPath() {
		return getSlashPath(Catalog.getCatalog());
	}

	public String getAttributeId(String name) {
		return getCatalogPath() + Catalog.PATH_SEPARATOR + name;
	}

	@SuppressWarnings("unchecked")
	public List<String> getCategories() {
		List<String> categories = new ArrayList<String>();
		if (null != get("category")) {
			categories.addAll((Collection<? extends String>) get("category"));
		}
		if (getParent(CatalogPage.class).hasCategories()) {
			categories.addAll(getParent(CatalogPage.class).getCategories());
		}
		return categories;
	}

	public boolean hasDescription() {
		return StringUtils.isNotEmpty(getDescription());
	}

	public String getDescription() {
		return getAttribute("description");
	}

	public boolean hasRules() {
		return getRules() != null && getRules().size() > 0;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getRules() {
		return (List<String>) get("rule");
	}

	public abstract Date getStart(Person person);

	public abstract Date getEnd(Person person);

	public abstract String getSpecifier(Person person);

}
