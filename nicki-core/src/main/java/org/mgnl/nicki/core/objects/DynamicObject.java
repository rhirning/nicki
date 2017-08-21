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
package org.mgnl.nicki.core.objects;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.TreeData;
import org.mgnl.nicki.core.helper.AttributeMapper;

public interface DynamicObject extends TreeData {
	String ATTRIBUTE_NAME = "name";
	String SEPARATOR = "/";

	enum STATUS {
		NEW,	// does not exist in target
		EXISTS,	// exists in target, but is not loaded  
		LOADED	// exists in target and is loaded
	}; 

//	void initNew(String parentPath, String namingValue);
	
	String getNamingValue();

	String getParentPath();

//	void initExisting(NickiContext context, String path);
	
//	void init(ContextSearchResult rs) throws DynamicObjectException;
	
//	private void init();
	
	boolean isNew();
	
	boolean isComplete();

	boolean attributeIsNotEmpty(String namingAttribute) ;

	String getPath();

	String getId();
	
	DataModel getModel();

	void setModel(DataModel model);

	/*
	private boolean checkAttribute(ContextSearchResult rs, String attribute,
			String value);
	*/

	<T extends DynamicObject> T getForeignKeyObject(Class<T> classDefinition, String key);

	<T extends DynamicObject> Collection<T> getForeignKeyObjects(Class<T> classDefinition, String key);

	void loadChildren();
	
	void unLoadChildren();
		
	List<? extends DynamicObject> getAllChildren();

	<T extends DynamicObject> Collection<T>  getChildren(Class<T> classDefinition);

	void addChild(String attribute, ChildFilter filter);
	
	String getAttribute(String attributeName);

	Object get(String key);
	
	Object get(Class<?> clazz, String key);
	
	void put(String key, Object value);
	
	void put(Class<?> clazz, String key, Object data);
	
	void listen(String key, Object value, Object oldValue);

	void remove(String key);

	void setOriginal(DynamicObject original);

	// Schema
	
	
	void addObjectClass(String objectClass);
	
	void addAdditionalObjectClass(String objectClass);
	
	void copyFrom(DynamicObject object);
	
	String getName();
	
	void update() throws DynamicObjectException;
	
	void update(String... attributeNames) throws DynamicObjectException;
	
	DynamicObject create() throws DynamicObjectException;

	void delete() throws DynamicObjectException;

	DynamicObject rename(String newName) throws DynamicObjectException;

	DynamicObject move(String newPath) throws DynamicObjectException;

	DynamicAttribute getDynamicAttribute(String name);

	String getSlashPath(TreeData parent);

	String getSlashPath(String parentPath);

	DynamicObject getParent();

	<T extends DynamicObject> T getParent(Class<T> classDefinition);

	<T extends DynamicAttribute >void addAttribute(T dynAttribute);

	void removeAttribute(String attributeName);
	
	void removeAdditionalObjectClass(String objectClass);
	
	void removeObjectClass(String objectClass);

	void setContext(NickiContext context);

	NickiContext getContext();

	List<String> merge(Map<DynamicAttribute, Object> changeAttributes);
	
	String getDisplayName();

	@Override
	boolean equals(Object obj);

	@Override
	int hashCode();

	String getInfo(String xml, String infoPath);
	
	String getLocalizedValue(String attributeName, String locale);
	
	String toString();

	<T extends DynamicObject> T getWritable();

	<T extends DynamicObject> T getAs(Class<T> classDefinition,
			DynamicObject dynamicObject);

	void clear(String key);

	boolean isModified();

	void setModified(boolean modified);
	
	Map<String, Object> getMap();
	
	STATUS getStatus();

	String getPath(String parentPath, String name);
	
	boolean isAnnotated();
	
	String getObjectClassFilter(NickiContext nickiContext);

	void init(ContextSearchResult rs) throws DynamicObjectException;

	void setStatus(STATUS new1);

	void setParentPath(String parentPath);

	void setPath(String path);
	
	JsonObjectBuilder toJsonObjectBuilder(AttributeMapper attributeMapper);
	JsonObject toJsonObject(AttributeMapper attributeMapper);

	List<String> merge(JsonObject query, AttributeMapper mapping, String... attributes);

}
