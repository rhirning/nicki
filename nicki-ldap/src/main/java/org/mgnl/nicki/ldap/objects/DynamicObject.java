package org.mgnl.nicki.ldap.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.ldap.helper.LdapHelper;

@SuppressWarnings("serial")
public abstract class DynamicObject implements Serializable, Cloneable {

	// Schema
	private DataModel model = new DataModel();
	private String path = null;
	private DynamicObject original = null;
	private DynamicObject parent = null;

	// Map with the attribute values
	private Map<String, Object> map = new HashMap<String, Object>();
	
	// cached attributes
	private Map<String, List<DynamicObject>> childObjects = null;
	
	private NickiContext context;
	
	protected DynamicObject() {
		initDataModel();
	}

	public void init(String parentPath, String namingValue) {
		put(model.getNamingAttribute(), namingValue);
		this.path = LdapHelper.getPath(parentPath, model.getNamingLdapAttribute(), namingValue);
	}
	
	public String getNamingValue() {
		return getAttribute(model.getNamingAttribute());
	}

	public String getParentPath() {
		return LdapHelper.getParentPath(path);
	}

	public void init(NickiContext context, ContextSearchResult rs) {
		this.context = context;
		this.path = LdapHelper.getUpcasePath(rs.getNameInNamespace());
		this.getModel().init(context, this, rs);
		this.original = (DynamicObject) this.clone();

	}
	
	public boolean isNew() {
		return (this.original == null);
	}
	
	public boolean isComplete() {
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
		for (Iterator<String> iterator = model.getAcceptors().keySet().iterator(); iterator.hasNext();) {
			String attribute = iterator.next();
			accepted &= checkAttribute(rs,attribute, model.getAcceptors().get(attribute));
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

	public void addAcceptor(String attribute, String value) {
		model.addAcceptor(attribute, value);
	}
	
	public DynamicObject getForeignKeyObject(String key) {
		String path = getAttribute(key);
		if (StringUtils.isNotEmpty(path)) {
			return context.loadObject(path);
		} else {
			return null;
		}
	}

	public void loadChildren() {
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

	public void addChild(String attribute, String filter) {
		model.addChild(attribute, filter);
	}
	
	// TODO
	public String getAttribute(String attributeName) {
		return (String) get(attributeName);
	}

	public Object get(String key) {
		return this.map.get(key);
	}
	public void put(String key, Object value) {
		listen(key, value, this.map.put(key, value));
	}
	
	public void listen(String key, Object value, Object oldValue) {
	}

	public void remove(String key) {
		if (this.map.containsKey(key)) {
			this.map.remove(key);
		}
	}
	
	// Schema
	
	public void addObjectClass(String objectClass) {
		model.addObjectClasses(objectClass);
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
	}
	
	@Override
	public DynamicObject clone() {
		DynamicObject cloned = null;
		try {
			cloned = context.getObjectFactory().getDynamicObject(this.getClass(), getParentPath(), getNamingValue());
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
		context.deleteObject(this);
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
		return getSlashPath(parentPath, getPath());
	}

	public static String getSlashPath(String parentPath, String childPath) {
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotEmpty(parentPath)) {
			childPath = StringUtils.substringBeforeLast(childPath, "," + parentPath);
		}
		
		String parts[] = StringUtils.split(childPath, ",");
		if (parts != null) {
			for (int i = parts.length - 1; i >=0; i--) {
				String part = StringUtils.substringAfter(parts[i], "=");
				sb.append("/");
				sb.append(part);
			}
		}
		return sb.toString();
	}

	public DynamicObject getParent() {
		if (parent == null) {
			parent = context.loadObject(getParentPath());
		}
		return parent;
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

	public Map<String, Object> getMap() {
		return map;
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


}
