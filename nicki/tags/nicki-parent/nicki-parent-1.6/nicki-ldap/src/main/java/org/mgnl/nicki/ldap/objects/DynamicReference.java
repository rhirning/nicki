package org.mgnl.nicki.ldap.objects;

import java.io.Serializable;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.methods.ReferenceMethod;

@SuppressWarnings("serial")
public class DynamicReference extends DynamicAttribute implements Serializable {

	private String attributeName;
	private String baseDn;
	public DynamicReference(String name, String baseDn, String attributeName, Class<?> attributeClass) {
		super(name, name, attributeClass);
		setVirtual();
		this.setBaseDn(baseDn);
		this.attributeName = attributeName;
	}
	public String getAttributeName() {
		return attributeName;
	}
	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}
	public String getBaseDn() {
		return baseDn;
	}
	@Override
	public void init(NickiContext context, DynamicObject dynamicObject, ContextSearchResult rs) {
		dynamicObject.put(getGetter(getName()), new ReferenceMethod(context, rs, this));
	}
}

