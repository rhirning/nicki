package org.mgnl.nicki.ldap.objects;

public interface DynamicObjectExtension {

	ExtensionModel getExtensionModel();
	
	DynamicObjectExtension clone();

	void setDynamicObject(DynamicObject dynamicObject);

}
