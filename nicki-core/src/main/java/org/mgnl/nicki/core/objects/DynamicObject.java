
package org.mgnl.nicki.core.objects;

/*-
 * #%L
 * nicki-core
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.util.Collection;
import java.util.List;
import java.util.Map;

import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.TreeData;
import org.mgnl.nicki.core.helper.AttributeMapper;

// TODO: Auto-generated Javadoc
/**
 * The Interface DynamicObject.
 */
public interface DynamicObject extends TreeData {
	
	/** The attribute name. */
	String ATTRIBUTE_NAME = "name";
	
	/** The separator. */
	String SEPARATOR = "/";

	/**
	 * The Enum STATUS.
	 */
	enum STATUS {
		
		/** The new. */
		NEW,	
	/** The exists. */
	// does not exist in target
		EXISTS,	
	/** The loaded. */
	// exists in target, but is not loaded  
		LOADED	// exists in target and is loaded
	}; 

//	void initNew(String parentPath, String namingValue);
	
	/**
 * Gets the naming value.
 *
 * @return the naming value
 */
String getNamingValue();

	/**
	 * Gets the parent path.
	 *
	 * @return the parent path
	 */
	String getParentPath();

//	void initExisting(NickiContext context, String path);
	
//	void init(ContextSearchResult rs) throws DynamicObjectException;
	
//	private void init();
	
	/**
 * Checks if is new.
 *
 * @return true, if is new
 */
boolean isNew();
	
	/**
	 * Checks if is complete.
	 *
	 * @return true, if is complete
	 */
	boolean isComplete();

	/**
	 * Attribute is not empty.
	 *
	 * @param namingAttribute the naming attribute
	 * @return true, if successful
	 */
	boolean attributeIsNotEmpty(String namingAttribute) ;

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	String getPath();

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	String getId();
	
	/**
	 * Gets the model.
	 *
	 * @return the model
	 */
	DataModel getModel();

	/**
	 * Sets the model.
	 *
	 * @param model the new model
	 */
	void setModel(DataModel model);

	/*
	private boolean checkAttribute(ContextSearchResult rs, String attribute,
			String value);
	*/

	/**
	 * Gets the foreign key object.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @param key the key
	 * @return the foreign key object
	 */
	<T extends DynamicObject> T getForeignKeyObject(Class<T> classDefinition, String key);

	/**
	 * Gets the foreign key objects.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @param key the key
	 * @return the foreign key objects
	 */
	<T extends DynamicObject> Collection<T> getForeignKeyObjects(Class<T> classDefinition, String key);

	/**
	 * Load children.
	 */
	void loadChildren();
	
	/**
	 * Un load children.
	 */
	void unLoadChildren();
		
	/**
	 * Gets the all children.
	 *
	 * @return the all children
	 */
	List<? extends DynamicObject> getAllChildren();

	/**
	 * Gets the children.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @return the children
	 */
	<T extends DynamicObject> Collection<T>  getChildren(Class<T> classDefinition);

	/**
	 * Adds the child.
	 *
	 * @param attribute the attribute
	 * @param filter the filter
	 */
	void addChild(String attribute, ChildFilter filter);
	
	/**
	 * Gets the attribute.
	 *
	 * @param attributeName the attribute name
	 * @return the attribute
	 */
	String getAttribute(String attributeName);

	/**
	 * Gets the.
	 *
	 * @param key the key
	 * @return the object
	 */
	Object get(String key);
	
	/**
	 * Gets the.
	 *
	 * @param clazz the clazz
	 * @param key the key
	 * @return the object
	 */
	Object get(Class<?> clazz, String key);
	
	/**
	 * Put.
	 *
	 * @param key the key
	 * @param value the value
	 */
	void put(String key, Object value);
	
	/**
	 * Put.
	 *
	 * @param clazz the clazz
	 * @param key the key
	 * @param data the data
	 */
	void put(Class<?> clazz, String key, Object data);
	
	/**
	 * Listen.
	 *
	 * @param key the key
	 * @param value the value
	 * @param oldValue the old value
	 */
	void listen(String key, Object value, Object oldValue);

	/**
	 * Removes the.
	 *
	 * @param key the key
	 */
	void remove(String key);

	/**
	 * Sets the original.
	 *
	 * @param original the new original
	 */
	void setOriginal(DynamicObject original);

	// Schema
	
	
	/**
	 * Adds the object class.
	 *
	 * @param objectClass the object class
	 */
	void addObjectClass(String objectClass);
	
	/**
	 * Adds the additional object class.
	 *
	 * @param objectClass the object class
	 */
	void addAdditionalObjectClass(String objectClass);
	
	/**
	 * Copy from.
	 *
	 * @param object the object
	 */
	void copyFrom(DynamicObject object);
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	String getName();
	
	/**
	 * Update.
	 *
	 * @throws DynamicObjectException the dynamic object exception
	 */
	void update() throws DynamicObjectException;
	
	/**
	 * Update.
	 *
	 * @param attributeNames the attribute names
	 * @throws DynamicObjectException the dynamic object exception
	 */
	void update(String... attributeNames) throws DynamicObjectException;
	
	/**
	 * Creates the.
	 *
	 * @return the dynamic object
	 * @throws DynamicObjectException the dynamic object exception
	 */
	DynamicObject create() throws DynamicObjectException;

	/**
	 * Delete.
	 *
	 * @throws DynamicObjectException the dynamic object exception
	 */
	void delete() throws DynamicObjectException;

	/**
	 * Rename.
	 *
	 * @param newName the new name
	 * @return the dynamic object
	 * @throws DynamicObjectException the dynamic object exception
	 */
	DynamicObject rename(String newName) throws DynamicObjectException;

	/**
	 * Move.
	 *
	 * @param newPath the new path
	 * @return the dynamic object
	 * @throws DynamicObjectException the dynamic object exception
	 */
	DynamicObject move(String newPath) throws DynamicObjectException;

	/**
	 * Gets the dynamic attribute.
	 *
	 * @param name the name
	 * @return the dynamic attribute
	 */
	DynamicAttribute getDynamicAttribute(String name);

	/**
	 * Gets the slash path.
	 *
	 * @param parent the parent
	 * @return the slash path
	 */
	String getSlashPath(TreeData parent);

	/**
	 * Gets the slash path.
	 *
	 * @param parentPath the parent path
	 * @return the slash path
	 */
	String getSlashPath(String parentPath);

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	DynamicObject getParent();

	/**
	 * Gets the parent.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @return the parent
	 */
	<T extends DynamicObject> T getParent(Class<T> classDefinition);

	/**
	 * Adds the attribute.
	 *
	 * @param <T> the generic type
	 * @param dynAttribute the dyn attribute
	 */
	<T extends DynamicAttribute >void addAttribute(T dynAttribute);

	/**
	 * Removes the attribute.
	 *
	 * @param attributeName the attribute name
	 */
	void removeAttribute(String attributeName);
	
	/**
	 * Removes the additional object class.
	 *
	 * @param objectClass the object class
	 */
	void removeAdditionalObjectClass(String objectClass);
	
	/**
	 * Removes the object class.
	 *
	 * @param objectClass the object class
	 */
	void removeObjectClass(String objectClass);

	/**
	 * Sets the context.
	 *
	 * @param context the new context
	 */
	void setContext(NickiContext context);

	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	NickiContext getContext();

	/**
	 * Merge.
	 *
	 * @param changeAttributes the change attributes
	 * @return the list
	 */
	List<String> merge(Map<DynamicAttribute, Object> changeAttributes);
	
	/**
	 * Gets the display name.
	 *
	 * @return the display name
	 */
	String getDisplayName();

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	@Override
	boolean equals(Object obj);

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	int hashCode();

	/**
	 * Gets the info.
	 *
	 * @param xml the xml
	 * @param infoPath the info path
	 * @return the info
	 */
	String getInfo(String xml, String infoPath);
	
	/**
	 * Gets the localized value.
	 *
	 * @param attributeName the attribute name
	 * @param locale the locale
	 * @return the localized value
	 */
	String getLocalizedValue(String attributeName, String locale);
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	String toString();

	/**
	 * Gets the writable.
	 *
	 * @param <T> the generic type
	 * @return the writable
	 */
	<T extends DynamicObject> T getWritable();

	/**
	 * Gets the as.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @param dynamicObject the dynamic object
	 * @return the as
	 */
	<T extends DynamicObject> T getAs(Class<T> classDefinition,
			DynamicObject dynamicObject);

	/**
	 * Clear.
	 *
	 * @param key the key
	 */
	void clear(String key);

	/**
	 * Checks if is modified.
	 *
	 * @return true, if is modified
	 */
	boolean isModified();

	/**
	 * Sets the modified.
	 *
	 * @param modified the new modified
	 */
	void setModified(boolean modified);
	
	/**
	 * Gets the map.
	 *
	 * @return the map
	 */
	Map<String, Object> getMap();
	
	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	STATUS getStatus();

	/**
	 * Gets the path.
	 *
	 * @param parentPath the parent path
	 * @param name the name
	 * @return the path
	 */
	String getPath(String parentPath, String name);
	
	/**
	 * Checks if is annotated.
	 *
	 * @return true, if is annotated
	 */
	boolean isAnnotated();
	
	/**
	 * Gets the object class filter.
	 *
	 * @param nickiContext the nicki context
	 * @return the object class filter
	 */
	String getObjectClassFilter(NickiContext nickiContext);

	/**
	 * Inits the.
	 *
	 * @param rs the rs
	 * @throws DynamicObjectException the dynamic object exception
	 */
	void init(ContextSearchResult rs) throws DynamicObjectException;

	/**
	 * Sets the status.
	 *
	 * @param new1 the new status
	 */
	void setStatus(STATUS new1);

	/**
	 * Sets the parent path.
	 *
	 * @param parentPath the new parent path
	 */
	void setParentPath(String parentPath);

	/**
	 * Sets the path.
	 *
	 * @param path the new path
	 */
	void setPath(String path);
	
	/**
	 * To json object builder.
	 *
	 * @param attributeMapper the attribute mapper
	 * @return the json object builder
	 */
	JsonObjectBuilder toJsonObjectBuilder(AttributeMapper attributeMapper);
	
	/**
	 * To json object.
	 *
	 * @param attributeMapper the attribute mapper
	 * @return the json object
	 */
	JsonObject toJsonObject(AttributeMapper attributeMapper);

	/**
	 * Merge.
	 *
	 * @param query the query
	 * @param mapping the mapping
	 * @param attributes the attributes
	 * @return the list
	 */
	List<String> merge(JsonObject query, AttributeMapper mapping, String... attributes);

}
