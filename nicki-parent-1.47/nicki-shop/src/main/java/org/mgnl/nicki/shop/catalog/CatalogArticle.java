/**
 * This file Copyright (c) 2011 deron Consulting GmbH
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.shop.catalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.XMLHelper;
import org.mgnl.nicki.dynamic.objects.objects.DynamicTemplateObject;
import org.mgnl.nicki.dynamic.objects.objects.Resource;
import org.mgnl.nicki.dynamic.objects.objects.Role;
import org.mgnl.nicki.dynamic.objects.reference.ReferenceDynamicAttribute;
import org.mgnl.nicki.dynamic.objects.types.TextArea;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;

@SuppressWarnings("serial")
public class CatalogArticle extends DynamicTemplateObject {

	public static enum TYPE {ROLE, RESOURCE, ARTICLE};
	
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
		
		dynAttribute = new DynamicAttribute("source", "nickiSource", String.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("category", "nickiCategory", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("rule", "nickiRule", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setEditorClass("org.mgnl.nicki.vaadin.base.rules.RuleAttributeField");
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("attributes", "nickiAttributes", TextArea.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new ReferenceDynamicAttribute(Role.class, "role", "nickiRoleRef", String.class,
				Config.getProperty("nicki.roles.basedn"));
		dynAttribute.setForeignKey(Role.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new ReferenceDynamicAttribute(Resource.class, "resource", "nickiResourceRef", String.class,
				Config.getProperty("nicki.resources.basedn"));
		dynAttribute.setForeignKey(Resource.class);
		addAttribute(dynAttribute);
	};

	public TYPE getArticleType() {
		if (StringUtils.isNotEmpty(getAttribute("role"))) {
			return TYPE.ROLE;
		} else if (StringUtils.isNotEmpty(getAttribute("resource"))) {
			return TYPE.RESOURCE;
		} else {
			return TYPE.ARTICLE;
		}
	}
	
	public void setAttributes(List<CatalogArticleAttribute> attributes) {
		put("attribute", attributes);
	}

	public static Collection<CatalogArticleAttribute> getFixedAttributes() {
		return fixedAttributes.values();
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

	public String getRolePath() {
		return getAttribute("role");
	}
	
	public String getApprovalPath() {
		return getAttribute("approval");
	}
	
	public String getResourcePath() {
		return getAttribute("resource");
	}

	public String getCatalogPath() {
		return getSlashPath(Catalog.getCatalog());
	}

	public String getAttributeId(String name) {
		return getCatalogPath() + Catalog.PATH_SEPARATOR + name;
	}

	public String getReferencePath() {
		if (StringUtils.isNotEmpty(getRolePath())) {
			return getRolePath();
		} else if (StringUtils.isNotEmpty(getResourcePath())) {
			return getResourcePath();
		} else {
			return getPath();
		}
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

}
