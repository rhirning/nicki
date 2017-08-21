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

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.data.TreeData;
import org.mgnl.nicki.core.helper.AttributeMapper;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.helper.PathHelper;
import org.mgnl.nicki.core.methods.ChildrenMethod;
import org.mgnl.nicki.core.methods.StructuredData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.TemplateMethodModel;

@SuppressWarnings("serial")
public class BaseDynamicObject implements DynamicObject, Serializable, Cloneable {
	private static final Logger LOG = LoggerFactory.getLogger(BaseDynamicObject.class);
	public static final String ATTRIBUTE_NAME = "name";
	public static final String SEPARATOR = "/";

	private String path;
	private String parentPath;
	private DynamicObject original;
	private DynamicObject parent;
	private STATUS status;
	private boolean modified;
	private DataModel model;
	
	// cached attributes
	private Map<String, Collection<DynamicObject>> childObjects;

	// Map with the attribute values
	private Map<String, Object> map = new HashMap<String, Object>();
		
	private NickiContext context;
	
	private boolean init;
	
	protected BaseDynamicObject() {
		// removed: must be called in TargetObjectFactory
		//		initDataModel();
	}
	
	public synchronized void init() {
		if (!init) {
			if (status == STATUS.EXISTS) {
				try {
					this.context.loadObject(this);
				} catch (DynamicObjectException e) {
					LOG.error("Error", e);
				}
			}
			init = true;
		}
	}
	
	public boolean isNew() {
		return this.status == STATUS.NEW;
	}
	
	public boolean isComplete() {
		init();
		return getModel().isComplete(this);
	}

	public boolean attributeIsNotEmpty(String attribute) {
		return StringUtils.isNotEmpty(getAttribute(attribute));
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return this.path;
	}

	public String getId() {
		return this.path;
	}

	public <T extends DynamicObject> T getForeignKeyObject(Class<T> classDefinition, String key) {
		String path = getAttribute(key);
		if (StringUtils.isNotEmpty(path)) {
			return context.loadObject(classDefinition, path);
		} else {
			return null;
		}
	}

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
					LOG.debug("Could not build object: " + path);
				}
			}
		}
		return objects;
	}

	public <T extends DynamicObject> Collection<T>  getChildren(Class<T> classDefinition) {
		init();
		return getContext().loadChildObjects(classDefinition, this, "");
	}

	public void addChild(String attribute, ChildFilter filter) {
		getModel().addChild(attribute, filter);
	}
	
	public String getAttribute(String attributeName) {
		init();
		return (String) get(attributeName);
	}

	public Object get(String key) {
		init();
		return this.map.get(key);
	}
	
	public Object get(Class<?> clazz, String key) {
		return get(clazz.getName() + "." + key);
	}
	
	public void put(String key, Object value) {
		init();
		listen(key, value, this.map.put(key, value));
	}
	
	public void put(Class<?> clazz, String key, Object data) {
		put(clazz.getName() + "." + key, data);
	}
	
	public void listen(String key, Object value, Object oldValue) {
	}

	public void remove(String key) {
		init();
		if (this.map.containsKey(key)) {
			this.map.remove(key);
		}
	}
	
	// Schema
	
	public void addObjectClass(String objectClass) {
		getModel().addObjectClasses(objectClass);
	}
	
	public void addAdditionalObjectClass(String objectClass) {
		getModel().addAdditionalObjectClasses(objectClass);
	}
	
	public void addAttribute(DynamicAttribute dynAttribute) {
		this.getModel().addAttribute(dynAttribute);
	}
	
	public void removeAttribute(String attributeName) {
		this.getModel().removeAttribute(attributeName);
	}
	
	public void removeAdditionalObjectClass(String objectClass) {
		this.getModel().removeAdditionalObjectClass(objectClass);
	}
	
	public void removeObjectClass(String objectClass) {
		this.getModel().removeObjectClass(objectClass);
	}

	public void addMethod(String name, TemplateMethodModel method) {
		put(DynamicAttribute.getGetter(name), method);
	}
	
	public Object execute(String methodName, @SuppressWarnings("rawtypes") List arguments) throws DynamicObjectException {
		try {
			TemplateMethodModel method = (TemplateMethodModel) get(methodName);
			return method.exec(arguments);
		} catch (Exception e) {
			throw new DynamicObjectException(e);
		}		
	}
	
	public void setOriginal(DynamicObject original) {
		this.original = original;
	}

	public DynamicObject getOriginal() {
		return original;
	}
	
	public void copyFrom(DynamicObject object) {
		this.setModel(object.getModel());
		this.map = object.getMap();
		this.status = object.getStatus();
	}
	
	@Override
	public DynamicObject clone() {
		DynamicObject cloned = null;
		try {
			cloned = context.getObjectFactory().getNewDynamicObject(this.getClass(), getParentPath(), getNamingValue());
			cloned.copyFrom(this);
		} catch (InstantiateDynamicObjectException e) {
			LOG.error("Error", e);
		}
		return cloned;
	}

	public String getName() {
		return getNamingValue();
	}
	
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
	
	public void update(String... attributeNames) throws DynamicObjectException {
		if (isNew()) {
			throw new DynamicObjectException("Object does not exist: " + getPath());
		}
		if (!StringUtils.equalsIgnoreCase(getPath(), getOriginal().getPath())) {
			throw new DynamicObjectException("Path has changed: " + getOriginal().getPath() + "->" + getPath());
		}
		context.updateObject(this, attributeNames);
	}
	
	public DynamicObject create() throws DynamicObjectException {
		if (!isNew() ||context.isExist(getPath())) {
			throw new DynamicObjectException("Object exists: " + getPath());
		}
		if (!isComplete()) {
			throw new DynamicObjectException("Object incomplete: " + getPath());
		}
		return context.createObject(this);
	}

	public void delete() throws DynamicObjectException {
		if (!isNew()) {
			context.deleteObject(this);
		}
	}

	public DynamicObject rename(String newName) throws DynamicObjectException {
		return context.renameObject(this, newName);
	}

	public void renameObject(String newName) throws DynamicObjectException {
		context.renameObject(this, newName);
	}

	public DynamicObject move(String newPath) throws DynamicObjectException {
		return context.moveObject(this, newPath);
	}

	public void moveTo(String newPath) throws DynamicObjectException {
		context.moveObject(this, newPath);
	}

	public DynamicAttribute getDynamicAttribute(String name) {
		return getModel().getDynamicAttribute(name);
	}

	public String getSlashPath(TreeData parent) {
		if (parent != null) {
			return getSlashPath(parent.getPath());
		} else {
			return getSlashPath("");
		}
	}

	public String getSlashPath(String parentPath) {
		return PathHelper.getSlashPath(parentPath, getPath());
	}

	public DynamicObject getParent() {
		if (parent == null) {
			parent = context.loadObject(getParentPath());
		}
		return parent;
	}

	protected void setParent(DynamicObject parent) {
		this.parent = parent;
	}

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
	/*
	public void addAttribute(DynamicAttribute dynAttribute) {
		this.getModel().addAttribute(dynAttribute);
	}
	 */
	public void setContext(NickiContext context) {
		this.context = context;
	}

	public NickiContext getContext() {
		return context;
	}

	@Override
	public List<String> merge(Map<DynamicAttribute, Object> changeAttributes) {
		List<String> modifiedAttributes = new ArrayList<>();
		for (DynamicAttribute dynamicAttribute : changeAttributes.keySet()) {
			put(dynamicAttribute.getName(), changeAttributes.get(dynamicAttribute));
		}
		return modifiedAttributes;
	}
	
	public String getDisplayName() {
		return getName();
	}

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

	@Override
	public int hashCode() {
		return getPath().hashCode();
	}


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
	
	public String toString() {
		return getDisplayName();
	}

	@SuppressWarnings("unchecked")
	public <T extends DynamicObject> T getWritable() {
		NickiContext ctx = this.getContext();
		ctx.setReadonly(false);
		return (T) ctx.loadObject(this.getClass(), this.getPath());
	}

	public <T extends DynamicObject> T getAs(Class<T> classDefinition,
			DynamicObject dynamicObject) {
		return getContext().loadObjectAs(classDefinition, this);
	}

	public void clear(String key) {
		put(key, null);
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	public void setStatus(STATUS status) {
		this.status = status;
	}

	public STATUS getStatus() {
		return status;
	}

	public Map<String, Object> getMap() {
		return map;
	}

	protected Map<String, Collection<DynamicObject>> getChildObjects() {
		return childObjects;
	}

	protected void initChildren() {
		childObjects = new HashMap<String, Collection<DynamicObject>>();
	}

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

	public void unLoadChildren() {
		this.childObjects = null;
	}
	
	public Collection<DynamicObject> getChildren(String key) {
		loadChildren();
		return childObjects.get(key);
	}
	
	public List<? extends DynamicObject> getAllChildren() {
		loadChildren();
		List<DynamicObject> list = new ArrayList<DynamicObject>();
		for (String key : childObjects.keySet()) {
			list.addAll(getChildren(key));
		}
		return list;
	}

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}
	
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

	@Override
	public boolean isAnnotated() {
		return getClass().isAnnotationPresent(org.mgnl.nicki.core.annotation.DynamicObject.class);
	}

	@Override
	public void setModel(DataModel model) {
		this.model = model;
	}

	@Override
	public DataModel getModel() {
		if (model == null) {
			model = new DataModel();
		}
		return model;
	}
	
	@Override
	public String getNamingValue() {
		return getAttribute(getModel().getNamingAttribute());
	}

	@Override
	public String getPath(String parentPath, String name) {
		return getContext().getAdapter().getPath(this, parentPath, name);
	}

	@Override
	public String getObjectClassFilter(NickiContext nickiContext) {
		return nickiContext.getAdapter().getObjectClassFilter(this);
	}

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
							LOG.debug("no getter for " + dynAttribute.getName());
						}
						List<String> list = null;
						if (getter != null) {
							try {
								list = (List<String>) getter.invoke(this);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								LOG.debug("wrongo getter for " + dynAttribute.getName(), e);
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
							LOG.debug("no getter for " + dynAttribute.getName());
						}
						Object value = null;
						if (getter != null) {
							try {
								value = getter.invoke(this);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								LOG.debug("wrongo getter for " + dynAttribute.getName(), e);
							}
						}
						if (value == null) {
							value = getAttribute(dynAttribute.getName());
						}
						if (value != null) {
							if (value instanceof String) {
								builder.add(key, (String) value);
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

	@Override
	public JsonObject toJsonObject(AttributeMapper mapping) {
		return toJsonObjectBuilder(mapping).build();
	}

	// TODO only supports flat data
	@Override
	public List<String> merge(JsonObject query, AttributeMapper mapping, String... attributes) {
		List<String> modifiedAttributes = new ArrayList<>();
		DataModel model = getModel();
		
		// Delete missing entries which are in attributes list
		if (attributes != null) {
			for(String key : attributes) {
				if (!query.containsKey(key)) {
					String attributeName;
					if (mapping != null) {
						if (mapping.isStrict() && !mapping.hasExternal(key)) {
							attributeName = null;
							LOG.error("External '" + key + "' missing in AttributeMapper");
						} else {
							attributeName = mapping.toInternal(key);
						}
					} else {
						attributeName = key;
					}

					put(attributeName, null);
					modifiedAttributes.add(attributeName);
				}
			}
		}
		
		
		for( String key : query.keySet()) {
			if (attributes == null || attributes.length == 0 || DataHelper.contains(attributes, key)) {
				String attributeName;
				if (mapping != null) {
					if (mapping.isStrict() && !mapping.hasExternal(key)) {
						attributeName = null;
						LOG.error("External '" + key + "' missing in AttributeMapper");
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
						put(attributeName, query.getString(key));
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

	@Override
	public boolean childrenAllowed() {
		return getModel().childrenAllowed();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends TreeData> T createChild(Class<T> classDefinition, String name) {
		try {
			return (T) createDynamicObjectChild((Class<DynamicObject>) classDefinition, name);
		} catch (InstantiateDynamicObjectException | DynamicObjectException e) {
			LOG.error("Error creating child");
		}
		return null;

	}

	public DynamicObject createDynamicObjectChild(Class<DynamicObject> classDefinition, String name) throws InstantiateDynamicObjectException, DynamicObjectException {

		return context.createDynamicObject(classDefinition,
				getPath(), name);
	}

	@Override
	public String getChildPath(TreeData parent, TreeData child) {

		DynamicObject object = (DynamicObject) child;
		return object.getContext().getAdapter().getPath(object, parent.getPath(), object.getNamingValue());
	}


}
