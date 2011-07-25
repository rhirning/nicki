package org.mgnl.nicki.ldap.context;

import java.io.Serializable;
import java.util.List;

import org.mgnl.nicki.ldap.auth.NickiPrincipal;
import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.ldap.objects.DynamicReference;

public interface NickiContext extends Serializable {
	public static enum READONLY {TRUE, FALSE};
	DynamicObject loadObject(String path);
	List<DynamicObject> loadObjects(String baseDn, String filter);
	List<DynamicObject> loadChildObjects(String parent,	String filter);
	List<DynamicObject> loadReferenceObjects(String path, DynamicReference reference);
	
	<T extends DynamicObject> T loadObject(Class<T> classDefinition, String path);
	<T extends DynamicObject> List<T> loadObjects(Class<T> classDefinition, String baseDn, String filter);
	<T extends DynamicObject> List<T> loadChildObjects(Class<T> classDefinition, String parent,	String filter);
	<T extends DynamicObject> List<T> loadReferenceObjects(Class<T> classDefinition, String path, DynamicReference reference);

	boolean isExist(String dn);
	<T extends DynamicObject> T createDynamicObject(Class<T> classDefinition, String parentPath, String namingValue)
			throws  InstantiateDynamicObjectException, DynamicObjectException;
	Target getTarget();
	void updateObject(DynamicObject dynamicObject) throws DynamicObjectException;
	DynamicObject createObject(DynamicObject dynamicObject) throws DynamicObjectException;
	void deleteObject(DynamicObject dynamicObject) throws DynamicObjectException;
	DynamicObject moveObject(DynamicObject dynamicObject, String newPath) throws DynamicObjectException;
	DynamicObject renameObject(DynamicObject dynamicObject, String newName) throws DynamicObjectException;
	void search(QueryHandler queryHandler) throws DynamicObjectException;
	ObjectFactory getObjectFactory();
	NickiPrincipal getPrincipal() throws DynamicObjectException;
	DynamicObject login(String user, String password);
	DynamicObject getUser();
	void setUser(DynamicObject user);
	String getObjectClassFilter(Class<? extends DynamicObject> classDefinition) throws InstantiateDynamicObjectException;

}
