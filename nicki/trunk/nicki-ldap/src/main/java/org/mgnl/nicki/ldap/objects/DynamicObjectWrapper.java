package org.mgnl.nicki.ldap.objects;

import java.util.Iterator;

import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;

import org.mgnl.nicki.ldap.core.JNDIWrapper;

public class DynamicObjectWrapper extends JNDIWrapper {

	public DynamicObjectWrapper(DynamicObject dynamicObject) {
		super();
		Attribute oc = new BasicAttribute("objectClass");
		for (Iterator<String> iterator = dynamicObject.getModel().getObjectClasses().iterator(); iterator.hasNext();) {
			String objectClass = iterator.next();
			oc.add(objectClass);
		}
		for (Iterator<String> iterator = dynamicObject.getModel().getAdditionalObjectClasses().iterator(); iterator.hasNext();) {
			String objectClass = iterator.next();
			oc.add(objectClass);
		}
		addAttribute(oc);
		for (Iterator<DynamicAttribute> iterator = dynamicObject.getModel().getMandatoryAttributes().iterator(); iterator.hasNext();) {
			DynamicAttribute dynAttribute =  iterator.next();
			if (dynAttribute.isMandatory() && !dynAttribute.isVirtual()) {
				if (dynAttribute.isStatic()) {
					StaticAttribute staticAttribute = (StaticAttribute) dynAttribute;
					addAttribute(dynAttribute.getLdapName(), staticAttribute.getValue());
				} else {
					addAttribute(dynAttribute.getLdapName(), dynamicObject.getAttribute(dynAttribute.getName()));
				}
			}			
		}
	}

}
