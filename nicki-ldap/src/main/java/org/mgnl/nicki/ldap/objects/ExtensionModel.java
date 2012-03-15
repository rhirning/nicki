package org.mgnl.nicki.ldap.objects;

import java.util.ArrayList;
import java.util.List;

public class ExtensionModel {
	private List<String> objectClasses = new ArrayList<String>();
	private List<String> additionalObjectClasses = new ArrayList<String>();
	private List<DynamicAttribute> dynamicAttributes = new ArrayList<DynamicAttribute>();

	public boolean hasObjectClasses() {
		return objectClasses != null && !objectClasses.isEmpty();
	}

	public boolean hasAdditionalObjectClasses() {
		return additionalObjectClasses != null && !additionalObjectClasses.isEmpty();
	}

	public List<String> getObjectClasses() {
		return objectClasses;
	}

	public void addObjectClass(String objectClass) {
		this.objectClasses.add(objectClass);
	}

	public void addAdditionalObjectClass(String additionalObjectClass) {
		this.additionalObjectClasses.add(additionalObjectClass);
	}

	public List<String> getAdditionalObjectClasses() {
		return additionalObjectClasses;
	}

	public void addDynamicAttribute(DynamicAttribute dynamicAttribute) {
		this.dynamicAttributes.add(dynamicAttribute);
	}

	public List<DynamicAttribute> getDynamicAttributes() {
		return dynamicAttributes;
	}

	public boolean hasDynamicAttributes() {
		return dynamicAttributes != null && !dynamicAttributes.isEmpty();
	}


}
