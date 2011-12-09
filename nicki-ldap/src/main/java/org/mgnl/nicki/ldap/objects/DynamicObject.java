/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.methods.StructuredData;

@SuppressWarnings("serial")
public abstract class DynamicObject implements Serializable, Cloneable {
	public static final String SEPARATOR = "/";

	public enum STATUS {
		NEW,	// does not exist in target
		EXISTS,	// exists in target, but is not loaded  
		LOADED	// exists in target and is loaded
	}; 

	// Schema
	private DataModel model = new DataModel();
	private String path = null;
	private DynamicObject original = null;
	private DynamicObject parent = null;
	
	private STATUS status;

	// Map with the attribute values
	private Map<String, Object> map = new HashMap<String, Object>();
	
	// cached attributes
	private Map<String, List<DynamicObject>> childObjects = null;
	
	private NickiContext context;
	
	protected DynamicObject() {
		initDataModel();
	}

	public void initNew(String parentPath, String namingValue) {
		this.status = STATUS.NEW;
		this.path = LdapHelper.getPath(parentPath, model.getNamingLdapAttribute(), namingValue);
		put(model.getNamingAttribute(), namingValue);
	}
	
	public String getNamingValue() {
		return getAttribute(model.getNamingAttribute());
	}

	public String getParentPath() {
		return LdapHelper.getParentPath(path);
	}

	public void initExisting(NickiContext context, String path) {
		this.status = STATUS.EXISTS;
		this.context = context;
		this.path = path;
		this.map.put(model.getNamingAttribute(), LdapHelper.getNamingValue(path));
	}
	
	public void init(ContextSearchResult rs) throws DynamicObjectException {
		if (status != STATUS.EXISTS) {
			throw new DynamicObjectException("Invalid call");
		}
		this.status = STATUS.LOADED;
		this.getModel().init(context, this, rs);
		this.original = (DynamicObject) this.clone();
	}
	
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
	
	public DataModel getModel() {
		return model;
	}

	public void setModel(DataModel model) {
		this.model = model;
	}

	public boolean accept(ContextSearchResult rs) {
		boolean accepted = true;
		for (Iterator<String> iterator = model.getObjectClasses().iterator(); iterator.hasNext();) {
			String objectClass = iterator.next();
			accepted &= checkAttribute(rs,"objectClass", objectClass);
		}
		return accepted;
	}

	private boolean checkAttribute(ContextSearchResult rs, String attribute,
			String value) {
		try {
			ContextAttribute attr = rs.getAttributes().get(attribute);
			for (Enumeration<Object> vals 
					= (Enumeration<Object>) attr.getAll(); vals.hasMoreElements();) {
				if (StringUtils.equalsIgnoreCase(value, (String) vals.nextElement())) {
					return true;
				}
		}
		} catch (Exception e) {
		}
		return false;
	}
	public abstract void initDataModel();

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

	public void loadChildren() {
		init();
		if (childObjects == null) {
			childObjects = new HashMap<String, List<DynamicObject>>();
			for (Iterator<String> iterator = model.getChildren().keySet().iterator(); iterator.hasNext();) {
				String key = iterator.next();
				String filter = model.getChildren().get(key);
				List<DynamicObject> list = context.loadChildObjects(path, filter);
				if (list != null) {
					childObjects.put(key, list);
				}
			}
		}
	}
	
	public void unLoadChildren() {
		this.childObjects = null;
	}
	
	public List<DynamicObject> getChildren(String key) {
		loadChildren();
		return childObjects.get(key);
	}
	
	public List<DynamicObject> getAllChildren() {
		loadChildren();
		List<DynamicObject> list = new ArrayList<DynamicObject>();
		for (Iterator<String> iterator = childObjects.keySet().iterator(); iterator.hasNext();) {
			list.addAll(getChildren(iterator.next()));
		}
		return list;
	}

	public <T extends DynamicObject> List<T>  getChildren(Class<T> classDefinition) {
		init();
		return getContext().loadChildObjects(classDefinition, this, "");
	}

	public void addChild(String attribute, String filter) {
		model.addChild(attribute, filter);
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
	public void put(String key, Object value) {
		init();
		listen(key, value, this.map.put(key, value));
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
		model.addObjectClasses(objectClass);
	}
	
	public void addAdditionalObjectClass(String objectClass) {
		model.addAdditionalObjectClasses(objectClass);
	}
	
	public void setOriginal(DynamicObject original) {
		this.original = original;
	}

	public DynamicObject getOriginal() {
		return original;
	}
	
	public void copyFrom(DynamicObject object) {
		this.model = object.getModel();
		this.map = object.map;
		this.status = object.status;
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

	public String getSlashPath(DynamicObject parent) {
		if (parent != null) {
			return getSlashPath(parent.getPath());
		} else {
			return getSlashPath("");
		}
	}

	public String getSlashPath(String parentPath) {
		return LdapHelper.getSlashPath(parentPath, getPath());
	}

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

	public void addAttribute(DynamicAttribute dynAttribute) {
		this.model.addAttribute(dynAttribute);
	}

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
		return getAttribute("name");
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


}
