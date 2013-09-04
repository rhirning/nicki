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

import java.util.List;
import java.util.Map;

import org.mgnl.nicki.core.context.NickiContext;

public interface DynamicObject {
	public static final String ATTRIBUTE_NAME = "name";
	public static final String SEPARATOR = "/";

	public enum STATUS {
		NEW,	// does not exist in target
		EXISTS,	// exists in target, but is not loaded  
		LOADED	// exists in target and is loaded
	}; 

//	public void initNew(String parentPath, String namingValue);
	
	public String getNamingValue();

	public String getParentPath();

//	public void initExisting(NickiContext context, String path);
	
//	public void init(ContextSearchResult rs) throws DynamicObjectException;
	
//	private void init();
	
	public boolean isNew();
	
	public boolean isComplete();

	public boolean attributeIsNotEmpty(String namingAttribute) ;

	public String getPath();

	public String getId();
	
	public DataModel getModel();

	public void setModel(DataModel model);

	/*
	private boolean checkAttribute(ContextSearchResult rs, String attribute,
			String value);
	*/
	public void initDataModel();

	public <T extends DynamicObject> T getForeignKeyObject(Class<T> classDefinition, String key);

	public <T extends DynamicObject> List<T> getForeignKeyObjects(Class<T> classDefinition, String key);

	public void loadChildren();
	
	public void unLoadChildren();
		
	public List<? extends DynamicObject> getAllChildren();

	public <T extends DynamicObject> List<T>  getChildren(Class<T> classDefinition);

	public void addChild(String attribute, ChildFilter filter);
	
	// TODO
	public String getAttribute(String attributeName);

	public Object get(String key);
	
	public Object get(Class<?> clazz, String key);
	
	public void put(String key, Object value);
	
	public void put(Class<?> clazz, String key, Object data);
	
	public void listen(String key, Object value, Object oldValue);

	public void remove(String key);

	public void setOriginal(DynamicObject original);

	// Schema
	
	
	public void addObjectClass(String objectClass);
	
	public void addAdditionalObjectClass(String objectClass);
	
	public void copyFrom(DynamicObject object);
	
	public String getName();
	
	public void update() throws DynamicObjectException;
	
	public void create() throws DynamicObjectException;

	public void delete() throws DynamicObjectException;

	public DynamicObject rename(String newName) throws DynamicObjectException;

	public DynamicObject move(String newPath) throws DynamicObjectException;

	public DynamicAttribute getDynamicAttribute(String name);

	public String getSlashPath(DynamicObject parent);

	public String getSlashPath(String parentPath);

	public DynamicObject getParent();

	public <T extends DynamicObject> T getParent(Class<T> classDefinition);

	public <T extends DynamicAttribute >void addAttribute(T dynAttribute);

	public void removeAttribute(String attributeName);

	public void setContext(NickiContext context);

	public NickiContext getContext();

	public void merge(Map<DynamicAttribute, Object> changeAttributes);
	
	public String getDisplayName();

	@Override
	public boolean equals(Object obj);

	@Override
	public int hashCode();

	public String getInfo(String xml, String infoPath);
	
	public String getLocalizedValue(String attributeName, String locale);
	
	public String toString();

	public <T extends DynamicObject> T getWritable();

	public <T extends DynamicObject> T getAs(Class<T> classDefinition,
			DynamicObject dynamicObject);

	public void clear(String key);

	public boolean isModified();

	public void setModified(boolean modified);
	
	public Map<String, Object> getMap();
	
	public STATUS getStatus();

	String getPath(String parentPath, String name);
	
	boolean isAnnotated();
	
	String getObjectClassFilter(NickiContext nickiContext);

	void init(ContextSearchResult rs) throws DynamicObjectException;

	public void setStatus(STATUS new1);

	public void setParentPath(String parentPath);

	public void setPath(String path);

}
