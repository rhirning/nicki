package org.mgnl.nicki.dynamic.objects.objects;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.dynamic.objects.reference.ReferenceDynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;

@SuppressWarnings("serial")
public class ShopArticle extends DynamicTemplateObject {
	
	private Shop shop = null;
	
	public static enum TYPE {ROLE, RESOURCE, ARTICLE};
	
	public void initDataModel() {
		addObjectClass("nickiShopArticle");
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
		return getSlashPath(shop);
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public Shop getShop() {
		return shop;
	}
}
