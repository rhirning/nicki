package org.mgnl.nicki.dynamic.objects.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mgnl.nicki.ldap.objects.DynamicAttribute;

@SuppressWarnings("serial")
public class CatalogPage extends DynamicTemplateObject {

	public void initDataModel() {
		addObjectClass("nickiCatalogPage");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("category", "nickiCategory", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("attribute", "nickiAttribute", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);
		
		// TODO
		addChild("page", "objectClass=nickiCatalogPage");
		addChild("article", "objectClass=nickiCatalogArticle");

	};
	
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

	public void setAttributes(List<String> attributes) {
		put("attribute", attributes);
	}

	public boolean hasAttributes() {
		return getAttributes() != null && getAttributes().size() != 0;
	}

	public boolean hasCategories() {
		return getCategories() != null && getCategories().size() != 0;
	}

	@SuppressWarnings("unchecked")
	public List<String> getCategories() {
		return (List<String>) get("category");
	}
	
	public void setCategories(List<String> categories) {
		put("category", categories);
	}


}
