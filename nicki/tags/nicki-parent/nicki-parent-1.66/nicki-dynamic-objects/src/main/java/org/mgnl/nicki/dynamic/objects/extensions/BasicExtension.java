package org.mgnl.nicki.dynamic.objects.extensions;

import org.mgnl.nicki.dynamic.objects.objects.Org;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectExtension;
import org.mgnl.nicki.ldap.objects.ExtensionModel;

public abstract class BasicExtension implements DynamicObjectExtension {

	private DynamicObject dynamicObject = null;

	ExtensionModel model = new ExtensionModel();

	public BasicExtension() {
		initExtensionModel();
	}

	@Override
	public ExtensionModel getExtensionModel() {
		return model;
	}

	public void initExtensionModel() {
		
	}
	
	public void addObjectClass(String objectClass) {
		model.addObjectClass(objectClass);
	}
	
	public void addAdditionalObjectClass(String additionalObjectClass) {
		model.addAdditionalObjectClass(additionalObjectClass);
	}

	public void addAttribute(DynamicAttribute dynamicAttribute) {
		model.addDynamicAttribute(dynamicAttribute);
	}

	@Override
	public DynamicObjectExtension clone() {
		try {
			return (DynamicObjectExtension) super.clone();
		} catch (CloneNotSupportedException e) {
		}
		return null;
	}


	public void setDynamicObject(DynamicObject dynamicObject) {
		this.dynamicObject = dynamicObject;
	}

	public DynamicObject getDynamicObject() {
		return dynamicObject;
	}
	
	public void put(String key, Object value) {
		getDynamicObject().put(key, value);
	}

	public Object get(String key) {
		return getDynamicObject().get(key);
	}
	
	public <T extends DynamicObject> T getForeignKeyObject(Class<T> classDefinition, String key) {
		return getDynamicObject().getForeignKeyObject(classDefinition, key);
	}

	public void clear(String key) {
		getDynamicObject().clear(key);
	}
	
	public Object extensionGet(String key) {
		return getDynamicObject().get(this.getClass(), key);
	}

	public void extensionPut(String key, Object value) {
		getDynamicObject().put(this.getClass(), key, value);
	}



}
