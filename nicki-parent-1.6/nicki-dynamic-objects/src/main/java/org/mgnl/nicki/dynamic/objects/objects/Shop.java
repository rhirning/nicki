package org.mgnl.nicki.dynamic.objects.objects;

import org.mgnl.nicki.ldap.methods.LoadObjectsMethod;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;

@SuppressWarnings("serial")
public class Shop extends DynamicTemplateObject {

	public void initDataModel() {
		// objectClass
		addObjectClass("nickiShop");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);
		
		dynAttribute = new DynamicAttribute("category", "nickiCategory", String.class);
		dynAttribute.setMultiple();
		addAttribute(dynAttribute);
		
		addChild("shelf", "(objectClass=nickiShopShelf)");
		
		addMethod("allArticles", new LoadObjectsMethod(getContext(), this, "(objectClass=nickiShopArticle)"));

	}

}
