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
import org.mgnl.nicki.dynamic.objects.reference.ReferenceDynamicAttribute;
import org.mgnl.nicki.dynamic.objects.types.TextArea;
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
		
		dynAttribute = new DynamicAttribute("attributes", "nickiAttributes", TextArea.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new ReferenceDynamicAttribute("role", "nickiRoleRef", String.class,
				"nicki.roles.basedn", "objectClass=nrfRole");
		dynAttribute.setForeignKey(Role.class);
		addAttribute(dynAttribute);
		
		dynAttribute = new ReferenceDynamicAttribute("resource", "nickiResourceRef", String.class,
				"nicki.resources.basedn", "objectClass=nrfResource");
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

	public boolean hasAttributes() {
		return getAllAttributes().size() > 0;
	}


	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[catalogArticle name='").append(getName());
		sb.append("' displayName='").append(getAttribute("displayName"));
		sb.append("']\n");
		if (hasAttributes()) {
			for (Iterator<CatalogArticleAttribute> iterator = getAllAttributes().iterator(); iterator.hasNext();) {
				sb.append(iterator.next().toString());
			}
		}
		return sb.toString();
	}


}
