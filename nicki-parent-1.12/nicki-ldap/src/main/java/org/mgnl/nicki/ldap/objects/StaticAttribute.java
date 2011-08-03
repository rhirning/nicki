package org.mgnl.nicki.ldap.objects;

@SuppressWarnings("serial")
public class StaticAttribute extends DynamicAttribute {
	private String value;

	public StaticAttribute(String name, String ldapName, Class<?> attributeClass, String value) {
		super(name, ldapName, attributeClass);
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
