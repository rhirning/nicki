package org.mgnl.nicki.dynamic.objects.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.dynamic.objects.reference.ReferenceDynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;

@SuppressWarnings("serial")
public class CatalogArticle extends DynamicTemplateObject {
	
	private Catalog catalog = null;
	
	public static enum TYPE {ROLE, RESOURCE, ARTICLE};
	
	public void initDataModel() {
		addObjectClass("nickiCatalogArticle");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("displayName", "displayName", String.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("description", "nickiDescription", String.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("category", "nickiCategory", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("attribute", "nickiAttribute", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);
		
		dynAttribute = new ReferenceDynamicAttribute("role", "nickiRoleRef", String.class,
				"nicki.roles.basedn", "objectClass=nrfRole");
		dynAttribute.setForeignKey();
		addAttribute(dynAttribute);
		
		dynAttribute = new ReferenceDynamicAttribute("resource", "nickiResourceRef", String.class,
				"nicki.resources.basedn", "objectClass=nrfResource");
		dynAttribute.setForeignKey();
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
	
	public String getCatalogPath() {
		return getSlashPath(catalog);
	}

	public void setCatalog(Catalog catalog) {
		this.catalog = catalog;
	}

	public Catalog getCatalog() {
		return catalog;
	}
	
	public void setAttributes(List<CatalogArticleAttribute> attributes) {
		put("attribute", attributes);
	}
	
	public List<CatalogArticleAttribute> getAttributes() {
		List<CatalogArticleAttribute> list = new ArrayList<CatalogArticleAttribute>();
		@SuppressWarnings("unchecked")
		List<String> attributes = (List<String>) get("attribute");
		if (attributes != null && attributes.size() > 0) {
			for (Iterator<String> iterator = attributes.iterator(); iterator.hasNext();) {
				try {
					list.add(new CatalogArticleAttribute(iterator.next()));
				} catch (Exception e) {
					e.printStackTrace();
				}
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


}
