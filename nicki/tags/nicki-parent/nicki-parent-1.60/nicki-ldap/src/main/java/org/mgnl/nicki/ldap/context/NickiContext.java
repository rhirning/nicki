/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.context;

import java.io.Serializable;
import java.util.List;

import org.mgnl.nicki.ldap.auth.NickiPrincipal;
import org.mgnl.nicki.ldap.core.LdapQuery;
import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

public interface NickiContext extends Serializable {
	public static enum READONLY {TRUE, FALSE};
	DynamicObject loadObject(String path);
	<T extends DynamicObject> T loadObject(Class<T> classDefinition, String path);
	
	List<DynamicObject> loadObjects(String baseDn, String filter);
	<T extends DynamicObject> List<T> loadObjects(Class<T> classDefinition, String baseDn, String filter);

	List<DynamicObject> loadChildObjects(String parent,	String filter);
	<T extends DynamicObject> T loadChildObject(Class<T> class1, DynamicObject parent, String childKey);
	<T extends DynamicObject> List<T> loadChildObjects(Class<T> classDefinition, String parent,	String filter);
	<T extends DynamicObject> List<T> loadChildObjects(Class<T> class1, DynamicObject parent, String filter);
	
	List<DynamicObject> loadReferenceObjects(LdapQuery query);
	<T extends DynamicObject> List<T> loadReferenceObjects(Class<T> classDefinition, LdapQuery query);

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
	void loadObject(DynamicObject dynamicObject) throws DynamicObjectException;
	void setReadonly(READONLY readonly);
	<T extends DynamicObject> T loadObjectAs(Class<T> classDefinition, DynamicObject dynamicObject);

}
