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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.objects.DynamicObjectException;

@SuppressWarnings("serial")
public abstract class BaseDynamicObject implements DynamicObject, Serializable, Cloneable {
	public static final String ATTRIBUTE_NAME = "name";
	public static final String SEPARATOR = "/";

	// Schema
	private String path = null;
	private DynamicObject original = null;
	private DynamicObject parent = null;
	
	private STATUS status;
	
	public void setStatus(STATUS status) {
		this.status = status;
	}

	private boolean modified = false;

	// Map with the attribute values
	private Map<String, Object> map = new HashMap<String, Object>();
		
	private NickiContext context;
	
	protected BaseDynamicObject() {
		// removed: must be called in TargetObjectFactory
		//		initDataModel();
	}
	
	public abstract DataModel getModel();

	/*
	public void initNew(String parentPath, String namingValue) {
		this.status = STATUS.NEW;
		this.path = LdapHelper.getPath(parentPath, getModel().getNamingLdapAttribute(), namingValue);
		put(getModel().getNamingAttribute(), namingValue);
	}
	
	public String getNamingValue() {
		return getAttribute(getModel().getNamingAttribute());
	}

	public String getParentPath() {
		return LdapHelper.getParentPath(path);
	}

	public void initExisting(NickiContext context, String path) {
		this.status = STATUS.EXISTS;
		this.context = context;
		this.path = path;
		this.map.put(getModel().getNamingAttribute(), LdapHelper.getNamingValue(path));
	}
	*/
	/*
	public void init(ContextSearchResult rs) throws DynamicObjectException {
		if (status != STATUS.EXISTS) {
			throw new DynamicObjectException("Invalid call");
		}
		this.status = STATUS.LOADED;
		this.getModel().init(context, this, rs);
		this.original = (DynamicObject) this.clone();
	}
	*/
	
	private void init() {
		if (status == STATUS.EXISTS) {
			try {
				this.context.loadObject(this);
			} catch (DynamicObjectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public boolean isNew() {
		return (this.status == STATUS.NEW);
	}
	
	public boolean isComplete() {
		init();
		return getModel().isComplete(this);
	}

	public boolean attributeIsNotEmpty(String namingAttribute) {
		return StringUtils.isNotEmpty(getAttribute(namingAttribute));
	}

	public String getPath() {
		return this.path;
	}

	public String getId() {
		return this.path;
	}

	/*
	public boolean accept(ContextSearchResult rs) {
		boolean accepted = true;
		for (Iterator<String> iterator = getModel().getObjectClasses().iterator(); iterator.hasNext();) {
			String objectClass = iterator.next();
			accepted &= checkAttribute(rs,"objectClass", objectClass);
		}
		return accepted;
	}
	*/

	public <T extends DynamicObject> T getForeignKeyObject(Class<T> classDefinition, String key) {
		String path = getAttribute(key);
		if (StringUtils.isNotEmpty(path)) {
			return context.loadObject(classDefinition, path);
		} else {
			return null;
		}
	}

	public <T extends DynamicObject> List<T> getForeignKeyObjects(Class<T> classDefinition, String key) {
		List<T> objects = new ArrayList<T>();
		@SuppressWarnings("unchecked")
		List<String> foreignKeys = (List<String>) get(key);
		for (Iterator<String> iterator = foreignKeys.iterator(); iterator.hasNext();) {
			String path = (String) iterator.next();
			DynamicObject object = context.loadObject(classDefinition, path);
			if (object != null) {
				objects.add(context.loadObject(classDefinition, path));
			} else {
				System.out.println("Could not build object: " + path);
			}
		}
		return objects;
	}

	/*
	public void loadChildren() {
		init();
		if (childObjects == null) {
			childObjects = new HashMap<String, List<DynamicObject>>();
			for (Iterator<String> iterator = getModel().getChildren().keySet().iterator(); iterator.hasNext();) {
				String key = iterator.next();
				String filter = getModel().getChildren().get(key);
				List<? extends DynamicObject> list = context.loadChildObjects(path, filter);
				if (list != null) {
					childObjects.put(key, list);
				}
			}
		}
	}
	*/
	
	/*
	public List<DynamicObject> getChildren(String key) {
		loadChildren();
		return childObjects.get(key);
	}
	*/
	/*
	public List<DynamicObject> getAllChildren() {
		loadChildren();
		List<DynamicObject> list = new ArrayList<DynamicObject>();
		for (Iterator<String> iterator = childObjects.keySet().iterator(); iterator.hasNext();) {
			list.addAll(getChildren(iterator.next()));
		}
		return list;
	}
*/
	public <T extends DynamicObject> List<T>  getChildren(Class<T> classDefinition) {
		init();
		return getContext().loadChildObjects(classDefinition, this, "");
	}

	public void addChild(String attribute, String filter) {
		getModel().addChild(attribute, filter);
	}
	
	// TODO
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
			e.printStackTrace();
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
	
	public void create() throws DynamicObjectException {
		if (!isNew() ||context.isExist(getPath())) {
			throw new DynamicObjectException("Object exists: " + getPath());
		}
		if (!isComplete()) {
			throw new DynamicObjectException("Object incomplete: " + getPath());
		}
		context.createObject(this);
	}

	public void delete() throws DynamicObjectException {
		if (!isNew()) {
			context.deleteObject(this);
		}
	}

	public DynamicObject rename(String newName) throws DynamicObjectException {
		return context.renameObject(this, newName);
	}

	public DynamicObject move(String newPath) throws DynamicObjectException {
		return context.moveObject(this, newPath);
	}

	public DynamicAttribute getDynamicAttribute(String name) {
		return getModel().getDynamicAttribute(name);
	}

	/*
	public String getSlashPath(DynamicObject parent) {
		if (parent != null) {
			return getSlashPath(parent.getPath());
		} else {
			return getSlashPath("");
		}
	}
	*/

	public DynamicObject getParent() {
		if (parent == null) {
			parent = context.loadObject(getParentPath());
		}
		return parent;
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

	public void merge(Map<DynamicAttribute, Object> changeAttributes) {
		for (Iterator<DynamicAttribute> iterator = changeAttributes.keySet().iterator(); iterator.hasNext();) {
			DynamicAttribute dynamicAttribute = iterator.next();
			put(dynamicAttribute.getName(), changeAttributes.get(dynamicAttribute));
		}
	};
	
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

	public abstract String getInfo(String xml, String infoPath);
	
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
	public <T extends DynamicObject> T getWritable(T dynamicObject) {
		NickiContext ctx = dynamicObject.getContext();
		ctx.setReadonly(false);
		return (T) ctx.loadObject(dynamicObject.getClass(), dynamicObject.getPath());
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

	public STATUS getStatus() {
		return status;
	}

	public Map<String, Object> getMap() {
		return map;
	}

}
