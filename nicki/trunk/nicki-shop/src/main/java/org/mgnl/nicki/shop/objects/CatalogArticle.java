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
package org.mgnl.nicki.shop.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.mgnl.nicki.core.helper.XMLHelper;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.shop.AssignedArticle;
import org.mgnl.nicki.dynamic.objects.types.TextArea;
import org.mgnl.nicki.ldap.annotations.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicTemplateObject;
import org.mgnl.nicki.shop.inventory.InventoryArticle;
import org.mgnl.nicki.shop.inventory.InventoryAttribute;

@SuppressWarnings("serial")
@DynamicObject(target="edir")
public class CatalogArticle extends DynamicTemplateObject {

	public static final String TYPE_ARTICLE = "ARTICLE";
	public static final String CAPTION_START = "nicki.rights.attribute.dateFrom.label";
	public static final String CAPTION_END = "nicki.rights.attribute.dateTo.label";

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

	public boolean hasArticle(Person person, CatalogArticle article) {
		for (InventoryArticle inventoryArticle : getInventoryArticles(person)) {
			if (inventoryArticle.getArticle().getPath() == article.getPath()) {
				return true;
			}
		}
		return false;
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
	
	@Override
	public String getDisplayName() {
		if (StringUtils.isNotEmpty((String) get("displayName"))) {
			return I18n.getText((String) get("displayName"));
		} else {
			return super.getDisplayName();
		}
	}

	public boolean isMultiple() {
		return false;
	}

	public List<InventoryArticle> getInventoryArticles(Person person) {
		List<InventoryArticle> result = new ArrayList<InventoryArticle>();
		// find all articles of that kind for person
		List<AssignedArticle> assignedArticles = person.getAssignedArticles();
		for (AssignedArticle assignedArticle : assignedArticles) {
			CatalogArticle catalogArticle = Catalog.getCatalog().getArticle(assignedArticle.getArticleId());
			Map<String, String> attributeMap = person.getCatalogAttributes(assignedArticle);
			List<InventoryAttribute> attributes = getInventoryAttributes(catalogArticle, attributeMap);
			result.add(new InventoryArticle(catalogArticle, assignedArticle, attributes));
		}
		return result;
	}
	
	public List<InventoryAttribute> getInventoryAttributes(CatalogArticle article,
			Map<String, String> attributeMap) {
		List<InventoryAttribute> attributes = new ArrayList<InventoryAttribute>();
		for (String key : attributeMap.keySet()) {
			for (CatalogArticleAttribute attribute : article.getAllAttributes()) {
				if (StringUtils.equals(key, attribute.getName())) {
					attributes.add(new InventoryAttribute(article, attribute, attributeMap.get(key)));
				}
			}
		}
		return attributes;
	}
}
