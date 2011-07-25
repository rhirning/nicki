package org.mgnl.nicki.dynamic.objects.objects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.mgnl.nicki.core.helper.XMLHelper;
import org.mgnl.nicki.dynamic.objects.types.TextArea;
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

		dynAttribute = new DynamicAttribute("attributes", "nickiAttributes", TextArea.class);
		addAttribute(dynAttribute);
		
		// TODO
		addChild("page", "objectClass=nickiCatalogPage");
		addChild("article", "objectClass=nickiCatalogArticle");

	};
	
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
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
