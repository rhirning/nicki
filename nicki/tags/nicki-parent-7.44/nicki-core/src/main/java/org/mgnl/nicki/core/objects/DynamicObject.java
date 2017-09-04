/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
