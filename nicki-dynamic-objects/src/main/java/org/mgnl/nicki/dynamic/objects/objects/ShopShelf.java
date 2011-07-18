package org.mgnl.nicki.dynamic.objects.objects;

import org.mgnl.nicki.ldap.objects.DynamicAttribute;

@SuppressWarnings("serial")
public class ShopShelf extends DynamicTemplateObject {

	public void initDataModel() {
		addObjectClass("nickiShopShelf");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("category", "nickiCategory", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);

		// TODO
		addChild("shelf", "objectClass=nickiShopShelf");
		addChild("article", "objectClass=nickiShopArticle");

	};

}
