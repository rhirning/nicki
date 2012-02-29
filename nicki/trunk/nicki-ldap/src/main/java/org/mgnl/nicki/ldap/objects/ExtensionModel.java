package org.mgnl.nicki.ldap.objects;

import java.util.List;

public class ExtensionModel {
	private List<String> objectClasses;
	private List<String> additionalObjectClasses;
	private List<DynamicAttribute> dynamicAttributes;

	public boolean hasObjectClasses() {
		return objectClasses != null && !objectClasses.isEmpty();
	}

	public boolean hasAdditionalObjectClasses() {
		return additionalObjectClasses != null && !additionalObjectClasses.isEmpty();
	}

	public List<String> getObjectClasses() {
		return objectClasses;
	}

	public void setObjectClasses(List<String> objectClasses) {
		this.objectClasses = objectClasses;
	}

	public void setAdditionalObjectClasses(List<String> additionalObjectClasses) {
		this.additionalObjectClasses = additionalObjectClasses;
	}

	public List<String> getAdditionalObjectClasses() {
		return additionalObjectClasses;
	}

	public void setDynamicAttributes(List<DynamicAttribute> dynamicAttributes) {
		this.dynamicAttributes = dynamicAttributes;
	}

	public List<DynamicAttribute> getDynamicAttributes() {
		return dynamicAttributes;
	}

	public boolean hasDynamicAttributes() {
		return dynamicAttributes != null && !dynamicAttributes.isEmpty();
	}

}
