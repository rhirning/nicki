
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


import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.data.TreeData;
import org.mgnl.nicki.core.helper.AttributeMapper;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.helper.PathHelper;
import org.mgnl.nicki.core.methods.ChildrenMethod;
import org.mgnl.nicki.core.methods.StructuredData;

import freemarker.template.TemplateMethodModelEx;
import lombok.extern.slf4j.Slf4j;


/**
 * The Class BaseDynamicObject.
 */
@Slf4j
@SuppressWarnings("serial")
public class BaseDynamicObject implements DynamicObject, Serializable, Cloneable {
	
	/** The Constant ATTRIBUTE_NAME. */
	public static final String ATTRIBUTE_NAME = "name";
	
	/** The Constant SEPARATOR. */
	public static final String SEPARATOR = "/";

	/** The path. */
	private String path;
	
	/** The parent path. */
	private String parentPath;
	
	/** The original. */
	private DynamicObject original;
	
	/** The parent. */
	private DynamicObject parent;
	
	/** The status. */
	private STATUS status;
	
	/** The modified. */
	private boolean modified;
	
	/** The model. */
	private DataModel model;
	
	/** The child objects. */
	// cached attributes
	private Map<String, Collection<DynamicObject>> childObjects;

	/** The map. */
	// Map with the attribute values
	private Map<String, Object> map = new HashMap<String, Object>();
		
	/** The context. */
	private NickiContext context;
	
	/** The init. */
	private boolean init;
	
	/**
	 * Instantiates a new base dynamic object.
	 */
	protected BaseDynamicObject() {
		// removed: must be called in TargetObjectFactory
		//		initDataModel();
	}
	
	/**
	 * Inits the.
	 */
	public synchronized void init() {
		if (!init) {
			if (status == STATUS.EXISTS) {
				try {
					this.context.loadObject(this);
				} catch (DynamicObjectException e) {
					log.error("Error", e);
				}
			}
			init = true;
		}
	}
	
	/**
	 * Checks if is new.
	 *
	 * @return true, if is new
	 */
	public boolean isNew() {
		return this.status == STATUS.NEW;
	}
	
	/**
	 * Checks if is complete.
	 *
	 * @return true, if is complete
	 */
	public boolean isComplete() {
		init();
		return getModel().isComplete(this);
	}

	/**
	 * Attribute is not empty.
	 *
	 * @param attribute the attribute
	 * @return true, if successful
	 */
	public boolean attributeIsNotEmpty(String attribute) {
		return StringUtils.isNotEmpty(getAttribute(attribute));
	}
	
	/**
	 * Sets the path.
	 *
	 * @param path the new path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return this.path;
	}

	/**
	 * Gets the foreign key object.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @param key the key
	 * @return the foreign key object
	 */
	public <T extends DynamicObject> T getForeignKeyObject(Class<T> classDefinition, String key) {
		String path = getAttribute(key);
		if (StringUtils.isNotEmpty(path)) {
			return context.loadObject(classDefinition, path);
		} else {
			return null;
		}
	}

	/**
	 * Gets the foreign key objects.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @param key the key
	 * @return the foreign key objects
	 */
	public <T extends DynamicObject> Collection<T> getForeignKeyObjects(Class<T> classDefinition, String key) {
		Collection<T> objects = new ArrayList<T>();
		@SuppressWarnings("unchecked")
		Collection<String> foreignKeys = (Collection<String>) get(key);
		if (foreignKeys != null) {
			for (String path : foreignKeys) {
				DynamicObject object = context.loadObject(classDefinition, path);
				if (object != null) {
					objects.add(context.loadObject(classDefinition, path));
				} else {
					log.debug("Could not build object: " + path);
				}
			}
		}
		return objects;
	}

	/**
	 * Gets the children.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @return the children
	 */
	public <T extends DynamicObject> Collection<T>  getChildren(Class<T> classDefinition) {
		init();
		return getContext().loadChildObjects(classDefinition, this, "");
	}

	/**
	 * Adds the child.
	 *
	 * @param attribute the attribute
	 * @param filter the filter
	 */
	public void addChild(String attribute, ChildFilter filter) {
		getModel().addChild(attribute, filter);
	}
	
	/**
	 * Gets the attribute.
	 *
	 * @param attributeName the attribute name
	 * @return the attribute
	 */
	public String getAttribute(String attributeName) {
		init();
		return (String) get(attributeName);
	}

	/**
	 * Gets the.
	 *
	 * @param key the key
	 * @return the object
	 */
	public Object get(String key) {
		init();
		return this.map.get(key);
	}
	
	/**
	 * Gets the.
	 *
	 * @param clazz the clazz
	 * @param key the key
	 * @return the object
	 */
	public Object get(Class<?> clazz, String key) {
		return get(clazz.getName() + "." + key);
	}
	
	/**
	 * Put.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void put(String key, Object value) {
		init();
		listen(key, value, this.map.put(key, value));
	}
	
	/**
	 * Put.
	 *
	 * @param clazz the clazz
	 * @param key the key
	 * @param data the data
	 */
	public void put(Class<?> clazz, String key, Object data) {
		put(clazz.getName() + "." + key, data);
	}
	
	/**
	 * Listen.
	 *
	 * @param key the key
	 * @param value the value
	 * @param oldValue the old value
	 */
	public void listen(String key, Object value, Object oldValue) {
	}

	/**
	 * Removes the.
	 *
	 * @param key the key
	 */
	public void remove(String key) {
		init();
		if (this.map.containsKey(key)) {
			this.map.remove(key);
		}
	}
	
	// Schema
	
	/**
	 * Adds the object class.
	 *
	 * @param objectClass the object class
	 */
	public void addObjectClass(String objectClass) {
		getModel().addObjectClasses(objectClass);
	}
	
	/**
	 * Adds the additional object class.
	 *
	 * @param objectClass the object class
	 */
	public void addAdditionalObjectClass(String objectClass) {
		getModel().addAdditionalObjectClasses(objectClass);
	}
	
	/**
	 * Adds the attribute.
	 *
	 * @param dynAttribute the dyn attribute
	 */
	public void addAttribute(DynamicAttribute dynAttribute) {
		this.getModel().addAttribute(dynAttribute);
	}
	
	/**
	 * Removes the attribute.
	 *
	 * @param attributeName the attribute name
	 */
	public void removeAttribute(String attributeName) {
		this.getModel().removeAttribute(attributeName);
	}
	
	/**
	 * Removes the additional object class.
	 *
	 * @param objectClass the object class
	 */
	public void removeAdditionalObjectClass(String objectClass) {
		this.getModel().removeAdditionalObjectClass(objectClass);
	}
	
	/**
	 * Removes the object class.
	 *
	 * @param objectClass the object class
	 */
	public void removeObjectClass(String objectClass) {
		this.getModel().removeObjectClass(objectClass);
	}

	/**
	 * Adds the method.
	 *
	 * @param name the name
	 * @param method the method
	 */
	public void addMethod(String name, TemplateMethodModelEx method) {
		put(DynamicAttribute.getGetter(name), method);
	}
	
	/**
	 * Execute.
	 *
	 * @param methodName the method name
	 * @param arguments the arguments
	 * @return the object
	 * @throws DynamicObjectException the dynamic object exception
	 */
	public Object execute(String methodName, @SuppressWarnings("rawtypes") List arguments) throws DynamicObjectException {
		try {
			TemplateMethodModelEx method = (TemplateMethodModelEx) get(methodName);
			return method.exec(arguments);
		} catch (Exception e) {
			throw new DynamicObjectException(e);
		}		
	}
	
	/**
	 * Sets the original.
	 *
	 * @param original the new original
	 */
	public void setOriginal(DynamicObject original) {
		this.original = original;
	}

	/**
	 * Gets the original.
	 *
	 * @return the original
	 */
	public DynamicObject getOriginal() {
		return original;
	}
	
	/**
	 * Copy from.
	 *
	 * @param object the object
	 */
	public void copyFrom(DynamicObject object) {
		this.setModel(object.getModel());
		this.map = object.getMap();
		this.status = object.getStatus();
	}
	
	/**
	 * Clone.
	 *
	 * @return the dynamic object
	 */
	@Override
	public DynamicObject clone() {
		DynamicObject cloned = null;
		try {
			cloned = context.getObjectFactory().getNewDynamicObject(this.getClass(), getParentPath(), getNamingValue());
			cloned.copyFrom(this);
		} catch (InstantiateDynamicObjectException e) {
			log.error("Error", e);
		}
		return cloned;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return getNamingValue();
	}
	
	/**
	 * Update.
	 *
	 * @throws DynamicObjectException the dynamic object exception
	 */
	public void update() throws DynamicObjectException {
		if (isNew()) {
			throw new DynamicObjectException("Object does not exist: " + getPath());
		}
		if (!isComplete()) {
			throw new DynamicObjectException("Object incomplete: " + getPath());
		}
		if (!StringUtils.equalsIgnoreCase(getPath(), getOriginal().getPath())) {
			throw new DynamicObjectException("Path has changed: " + getOriginal().getPath() + "->" + getPath());
		}
		context.updateObject(this);
	}
	
	/**
	 * Update.
	 *
	 * @param attributeNames the attribute names
	 * @throws DynamicObjectException the dynamic object exception
	 */
	public void update(String... attributeNames) throws DynamicObjectException {
		if (isNew()) {
			throw new DynamicObjectException("Object does not exist: " + getPath());
		}
		if (!StringUtils.equalsIgnoreCase(getPath(), getOriginal().getPath())) {
			throw new DynamicObjectException("Path has changed: " + getOriginal().getPath() + "->" + getPath());
		}
		context.updateObject(this, attributeNames);
	}
	
	/**
	 * Creates the.
	 *
	 * @return the dynamic object
	 * @throws DynamicObjectException the dynamic object exception
	 */
	public DynamicObject create() throws DynamicObjectException {
		if (!isNew() ||context.isExist(getPath())) {
			throw new DynamicObjectException("Object exists: " + getPath());
		}
		if (!isComplete()) {
			throw new DynamicObjectException("Object incomplete: " + getPath());
		}
		return context.createObject(this);
	}

	/**
	 * Delete.
	 *
	 * @throws DynamicObjectException the dynamic object exception
	 */
	public void delete() throws DynamicObjectException {
		if (!isNew()) {
			context.deleteObject(this);
		}
	}

	/**
	 * Rename.
	 *
	 * @param newName the new name
	 * @return the dynamic object
	 * @throws DynamicObjectException the dynamic object exception
	 */
	public DynamicObject rename(String newName) throws DynamicObjectException {
		return context.renameObject(this, newName);
	}

	/**
	 * Rename object.
	 *
	 * @param newName the new name
	 * @throws DynamicObjectException the dynamic object exception
	 */
	public void renameObject(String newName) throws DynamicObjectException {
		context.renameObject(this, newName);
	}

	/**
	 * Move.
	 *
	 * @param newPath the new path
	 * @return the dynamic object
	 * @throws DynamicObjectException the dynamic object exception
	 */
	public DynamicObject move(String newPath) throws DynamicObjectException {
		return context.moveObject(this, newPath);
	}

	/**
	 * Move to.
	 *
	 * @param newPath the new path
	 * @throws DynamicObjectException the dynamic object exception
	 */
	public void moveTo(String newPath) throws DynamicObjectException {
		context.moveObject(this, newPath);
	}

	/**
	 * Gets the dynamic attribute.
	 *
	 * @param name the name
	 * @return the dynamic attribute
	 */
	public DynamicAttribute getDynamicAttribute(String name) {
		return getModel().getDynamicAttribute(name);
	}

	/**
	 * Gets the slash path.
	 *
	 * @param parent the parent
	 * @return the slash path
	 */
	public String getSlashPath(TreeData parent) {
		if (parent != null) {
			return getSlashPath(parent.getPath());
		} else {
			return getSlashPath("");
		}
	}

	/**
	 * Gets the slash path.
	 *
	 * @param parentPath the parent path
	 * @return the slash path
	 */
	public String getSlashPath(String parentPath) {
		return PathHelper.getSlashPath(parentPath, getPath());
	}

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	public DynamicObject getParent() {
		if (parent == null) {
			parent = context.loadObject(getParentPath());
		}
		return parent;
	}

	/**
	 * Sets the parent.
	 *
	 * @param parent the new parent
	 */
	protected void setParent(DynamicObject parent) {
		this.parent = parent;
	}

	/**
	 * Gets the parent.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @return the parent
	 */
	@SuppressWarnings("unchecked")
	public <T extends DynamicObject> T getParent(Class<T> classDefinition) {
		if (parent == null) {
			parent = context.loadObject(getParentPath());
		}
		try {
			return (T) parent;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Sets the context.
	 *
	 * @param context the new context
	 */
	/*
	public void addAttribute(DynamicAttribute dynAttribute) {
		this.getModel().addAttribute(dynAttribute);
	}
	 */
	public void setContext(NickiContext context) {
		this.context = context;
	}

	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	public NickiContext getContext() {
		return context;
	}

	/**
	 * Merge.
	 *
	 * @param changeAttributes the change attributes
	 * @return the list
	 */
	@Override
	public List<String> merge(Map<DynamicAttribute, Object> changeAttributes) {
		List<String> modifiedAttributes = new ArrayList<>();
		for (DynamicAttribute dynamicAttribute : changeAttributes.keySet()) {
			put(dynamicAttribute.getName(), changeAttributes.get(dynamicAttribute));
		}
		return modifiedAttributes;
	}
	
	/**
	 * Gets the display name.
	 *
	 * @return the display name
	 */
	public String getDisplayName() {
		return getName();
	}

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	@Override
	public boolean equals(Object obj) {
		try {
			if (StringUtils.equalsIgnoreCase(getPath(), ((DynamicObject)obj).getPath())) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		return getPath().hashCode();
	}


	/**
	 * Gets the info.
	 *
	 * @param xml the xml
	 * @param infoPath the info path
	 * @return the info
	 */
	public String getInfo(String xml, String infoPath) {
		String parts[] = StringUtils.split(infoPath, SEPARATOR);
		if (parts.length < 2) {
			return null;
		}
		// correct xml
		StringUtils.replace(xml, "&lt;", "<");
		StringUtils.replace(xml, "&gt;", ">");
		StructuredData data  = new StructuredData(xml); 
		try {
			Element element = data.getDocument().getRootElement();
			int i = 1;
			while (i < parts.length - 1) {
				element = element.getChild(parts[i]);
				i++;
			}
			
			return element.getChildTextTrim(parts[i]);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Gets the localized value.
	 *
	 * @param attributeName the attribute name
	 * @param locale the locale
	 * @return the localized value
	 */
	public String getLocalizedValue(String attributeName, String locale) {
		Map<String, String> valueMap = DataHelper.getMap(getAttribute(attributeName), "|", "~");
		if (valueMap.size() == 0) {
			return null;
		} else if (valueMap.containsKey(locale)) {
			return valueMap.get(locale);
		} else {
			return valueMap.values().iterator().next();
		}
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	public String toString() {
		return getDisplayName();
	}

	/**
	 * Gets the writable.
	 *
	 * @param <T> the generic type
	 * @return the writable
	 */
	@SuppressWarnings("unchecked")
	public <T extends DynamicObject> T getWritable() {
		NickiContext ctx = this.getContext();
		ctx.setReadonly(false);
		return (T) ctx.loadObject(this.getClass(), this.getPath());
	}

	/**
	 * Gets the as.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @param dynamicObject the dynamic object
	 * @return the as
	 */
	public <T extends DynamicObject> T getAs(Class<T> classDefinition,
			DynamicObject dynamicObject) {
		return getContext().loadObjectAs(classDefinition, this);
	}

	/**
	 * Clear.
	 *
	 * @param key the key
	 */
	public void clear(String key) {
		put(key, null);
	}

	/**
	 * Checks if is modified.
	 *
	 * @return true, if is modified
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * Sets the modified.
	 *
	 * @param modified the new modified
	 */
	public void setModified(boolean modified) {
		this.modified = modified;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(STATUS status) {
		this.status = status;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public STATUS getStatus() {
		return status;
	}

	/**
	 * Gets the map.
	 *
	 * @return the map
	 */
	public Map<String, Object> getMap() {
		return map;
	}

	/**
	 * Gets the child objects.
	 *
	 * @return the child objects
	 */
	protected Map<String, Collection<DynamicObject>> getChildObjects() {
		return childObjects;
	}

	/**
	 * Inits the children.
	 */
	protected void initChildren() {
		childObjects = new HashMap<String, Collection<DynamicObject>>();
	}

	/**
	 * Load children.
	 */
	public void loadChildren() {
		init();
		if (getChildObjects() == null) {
			initChildren();
			for (String key : getModel().getChildren().keySet()) {
				ChildFilter filter = getModel().getChildren().get(key);
				@SuppressWarnings("unchecked")
				Collection<DynamicObject> list = (Collection<DynamicObject>) getContext().loadChildObjects(getPath(), filter);
				if (list != null) {
					getChildObjects().put(key, list);
				}
			}
		}
	}

	/**
	 * Un load children.
	 */
	public void unLoadChildren() {
		this.childObjects = null;
	}
	
	/**
	 * Gets the children.
	 *
	 * @param key the key
	 * @return the children
	 */
	public Collection<DynamicObject> getChildren(String key) {
		loadChildren();
		return childObjects.get(key);
	}
	
	/**
	 * Gets the all children.
	 *
	 * @return the all children
	 */
	public List<? extends DynamicObject> getAllChildren() {
		loadChildren();
		List<DynamicObject> list = new ArrayList<DynamicObject>();
		for (String key : childObjects.keySet()) {
			list.addAll(getChildren(key));
		}
		return list;
	}

	/**
	 * Gets the parent path.
	 *
	 * @return the parent path
	 */
	public String getParentPath() {
		return parentPath;
	}

	/**
	 * Sets the parent path.
	 *
	 * @param parentPath the new parent path
	 */
	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}
	
	/**
	 * Inits the.
	 *
	 * @param rs the rs
	 * @throws DynamicObjectException the dynamic object exception
	 */
	@Override
	public void init(ContextSearchResult rs) throws DynamicObjectException {
		if (getStatus() != STATUS.EXISTS) {
			throw new DynamicObjectException("Invalid call");
		}
		this.setStatus(STATUS.LOADED);
		this.getModel().init(getContext(), this, rs);
		setOriginal((DynamicObject) this.clone());

		for (String key : getModel().getChildren().keySet()) {
			ChildFilter filter = getModel().getChildren().get(key);
			put(DynamicAttribute.getGetter(key), new ChildrenMethod(getContext(), rs, filter));
		}
	}

	/**
	 * Checks if is annotated.
	 *
	 * @return true, if is annotated
	 */
	@Override
	public boolean isAnnotated() {
		return getClass().isAnnotationPresent(org.mgnl.nicki.core.annotation.DynamicObject.class);
	}

	/**
	 * Sets the model.
	 *
	 * @param model the new model
	 */
	@Override
	public void setModel(DataModel model) {
		this.model = model;
	}

	/**
	 * Gets the model.
	 *
	 * @return the model
	 */
	@Override
	public DataModel getModel() {
		if (model == null) {
			model = new DataModel();
		}
		return model;
	}
	
	/**
	 * Gets the naming value.
	 *
	 * @return the naming value
	 */
	@Override
	public String getNamingValue() {
		return getAttribute(getModel().getNamingAttribute());
	}

	/**
	 * Gets the path.
	 *
	 * @param parentPath the parent path
	 * @param name the name
	 * @return the path
	 */
	@Override
	public String getPath(String parentPath, String name) {
		return getContext().getAdapter().getPath(this, parentPath, name);
	}

	/**
	 * Gets the object class filter.
	 *
	 * @param nickiContext the nicki context
	 * @return the object class filter
	 */
	@Override
	public String getObjectClassFilter(NickiContext nickiContext) {
		return nickiContext.getAdapter().getObjectClassFilter(this);
	}

	/**
	 * To json object builder.
	 *
	 * @param mapping the mapping
	 * @return the json object builder
	 */
	@SuppressWarnings("unchecked")
	@Override
	public JsonObjectBuilder toJsonObjectBuilder(AttributeMapper mapping) {
		DataModel model = getModel();
		JsonObjectBuilder builder = Json.createObjectBuilder();
		for (DynamicAttribute dynAttribute : model.getAttributes().values()) {

			if (mapping == null || !mapping.isHiddenInternal(dynAttribute.getName())) {
				String key;
				if (mapping != null) {
					if (mapping.isStrict() && !mapping.hasInternal(dynAttribute.getName())) {
						key = null;
					} else {
						key = mapping.toExternal(dynAttribute.getName());
					}
				} else {
					key = dynAttribute.getName();
				}
				if (key != null) {
					if (dynAttribute.isMultiple()) {
						Method getter = null;
						try {
							getter = getClass().getMethod(DynamicAttribute.getMultipleGetter(dynAttribute.getName()));
						} catch (NoSuchMethodException | SecurityException e) {
							log.debug("no getter for " + dynAttribute.getName());
						}
						List<String> list = null;
						if (getter != null) {
							try {
								list = (List<String>) getter.invoke(this);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								log.debug("wrongo getter for " + dynAttribute.getName(), e);
							}
						}
						if (list == null) {
							list = (List<String>) get(dynAttribute.getName());
						}
						if (list != null && list.size() > 0) {
							JsonArrayBuilder lb = Json.createArrayBuilder();
							for (String entry : list) {
								lb.add(entry);
							}
							builder.add(key, lb);
						}
					} else {
						Method getter = null;
						try {
							getter = getClass().getMethod(DynamicAttribute.getGetter(dynAttribute.getName()));
						} catch (NoSuchMethodException | SecurityException e) {
							log.debug("no getter for " + dynAttribute.getName());
						}
						Object value = null;
						if (getter != null) {
							try {
								value = getter.invoke(this);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								log.debug("wrong getter for " + dynAttribute.getName(), e);
							}
						}
						if (value == null) {
							value = getAttribute(dynAttribute.getName());
						}
						if (value != null) {
							if (value instanceof String) {
								builder.add(key, mapping.correctValue(key, (String) value));
							} else if (value instanceof Date) {
								if (StringUtils.isNotBlank(dynAttribute.getFormat())) {
									SimpleDateFormat format = new SimpleDateFormat(dynAttribute.getFormat());
									builder.add(key, format.format((Date)value));
								} else {
									builder.add(key, DataHelper.getMilli((Date)value));
								}
							} else if (value instanceof Boolean) {
								builder.add(key, ((Boolean) value).toString());
							}
						}
					}
				}
			}
		}
		return builder;
	}

	/**
	 * To json object.
	 *
	 * @param mapping the mapping
	 * @return the json object
	 */
	@Override
	public JsonObject toJsonObject(AttributeMapper mapping) {
		return toJsonObjectBuilder(mapping).build();
	}

	// TODO only supports flat data
	/*
	 * String... attributes		Names in Json query
	 * (non-Javadoc)
	 * @see org.mgnl.nicki.core.objects.DynamicObject#merge(javax.json.JsonObject, org.mgnl.nicki.core.helper.AttributeMapper, java.lang.String[])
	 */
	
	/**
	 * Merge.
	 *
	 * @param query the query
	 * @param mapping the mapping
	 * @param attributes the attributes
	 * @return the list
	 */
	@Override
	public List<String> merge(JsonObject query, AttributeMapper mapping, String... attributes) {
		List<String> modifiedAttributes = new ArrayList<>();
		DataModel model = getModel();
		
		// Delete missing entries which are in attributes list
		if (attributes != null) {
			for(String key : attributes) {
				String internalName = key;
				if (mapping != null && mapping.hasExternal(key)) {
					internalName = mapping.toInternal(key);
				}
				if (!query.containsKey(key)) {
					if (mapping != null) {
						if (mapping.isStrict() && !mapping.hasExternal(key)) {
							log.error("External '" + key + "' missing in AttributeMapper");
						}
					}

					put(internalName, null);
					modifiedAttributes.add(internalName);
				}
			}
		}
		
		
		for( String key : query.keySet()) {
			if (attributes == null || attributes.length == 0 || DataHelper.contains(attributes, key)) {
				String attributeName;
				if (mapping != null) {
					if (mapping.isStrict() && !mapping.hasExternal(key)) {
						attributeName = null;
						log.info("External '" + key + "' missing in AttributeMapper");
					} else {
						attributeName = mapping.toInternal(key);
					}
				} else {
					attributeName = key;
				}
				
				DynamicAttribute dynAttribute = model.getDynamicAttribute(attributeName);
				if (dynAttribute != null) {
					Class<?> type = dynAttribute.getType();
					if (type == String.class) {
						if (dynAttribute.isMultiple()) {
							List<String> list = new ArrayList<>();
							if (query.containsKey(key)) {
								JsonArray jsonArray = query.getJsonArray(key);
								for (int i = 0; i < jsonArray.size(); i++) {
									list.add(jsonArray.getString(i));
								}
							}
							put(attributeName, list);
						} else {
							put(attributeName, query.getString(key));
						}
					} else if (type == Date.class) {
						put(attributeName, query.getString(key));
					} else if (type == Boolean.class) {
						put(attributeName, query.getString(key));
					} else{
						put(attributeName, query.getString(key));
					}
					modifiedAttributes.add(attributeName);
				}
			}
		}
		return modifiedAttributes;
	}

	/**
	 * Children allowed.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean childrenAllowed() {
		return getModel().childrenAllowed();
	}

	/**
	 * Creates the child.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @param name the name
	 * @return the t
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends TreeData> T createChild(Class<T> classDefinition, String name) {
		try {
			return (T) createDynamicObjectChild((Class<DynamicObject>) classDefinition, name);
		} catch (InstantiateDynamicObjectException | DynamicObjectException e) {
			log.error("Error creating child");
		}
		return null;

	}

	/**
	 * Creates the dynamic object child.
	 *
	 * @param classDefinition the class definition
	 * @param name the name
	 * @return the dynamic object
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 * @throws DynamicObjectException the dynamic object exception
	 */
	public DynamicObject createDynamicObjectChild(Class<DynamicObject> classDefinition, String name) throws InstantiateDynamicObjectException, DynamicObjectException {

		return context.createDynamicObject(classDefinition,
				getPath(), name);
	}

	/**
	 * Gets the child path.
	 *
	 * @param parent the parent
	 * @param child the child
	 * @return the child path
	 */
	@Override
	public String getChildPath(TreeData parent, TreeData child) {

		DynamicObject object = (DynamicObject) child;
		return object.getContext().getAdapter().getPath(object, parent.getPath(), object.getNamingValue());
	}


}
