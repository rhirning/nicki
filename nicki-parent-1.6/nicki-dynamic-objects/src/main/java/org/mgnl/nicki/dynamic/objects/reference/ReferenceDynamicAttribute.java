package org.mgnl.nicki.dynamic.objects.reference;

import java.util.List;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;

@SuppressWarnings("serial")
public class ReferenceDynamicAttribute extends DynamicAttribute {
	String baseDn;
	String filter;
	public ReferenceDynamicAttribute(String name, String ldapName, Class<?> attributeClass,
			String baseDnKey,	String filter) {
		super(name, ldapName, attributeClass);
		this.baseDn = Config.getProperty(baseDnKey);
		this.filter = filter;
	}

	@Override
	public List<DynamicObject> getOptions(DynamicObject dynamicObject) {
		return dynamicObject.getContext().loadObjects(baseDn, filter);
	}

}
