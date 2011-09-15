package org.mgnl.nicki.dynamic.objects.objects;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.dynamic.objects.reference.ReferenceDynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;


@SuppressWarnings("serial")
public class ResourceAssociation extends DynamicTemplateObject {

	@Override
	public void initDataModel() {
		addObjectClass("nrfResourceAssociation");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new ReferenceDynamicAttribute(Resource.class, "resource", "nrfResource", String.class,
				Config.getProperty("nicki.system.basedn"));
		dynAttribute.setForeignKey(Resource.class);
		addAttribute(dynAttribute);
	}
	
	public String toString() {
		return getAttribute("name");
	}

}
