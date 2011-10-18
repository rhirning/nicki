package org.mgnl.nicki.vaadin.base.fields;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.ldap.objects.DynamicObject;

public class BaseDynamicAttributeField {

	public String getName(DynamicObject dynamicObject, String attributeName) {
		String key = "nicki.dynamic-objects." 
			+ StringUtils.substringBefore(dynamicObject.getClass().getSimpleName(), ".")
			+ "."
			+ attributeName;
		String name = I18n.getText(key);
		if (StringUtils.equals(key, name)) {
			name = attributeName;
		}
		return name;
	}
}
