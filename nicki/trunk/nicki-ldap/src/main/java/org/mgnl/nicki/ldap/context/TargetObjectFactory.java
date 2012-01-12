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

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

public class TargetObjectFactory implements ObjectFactory {

	public static final String PROPERTY_BASE = "nicki.template.objectfactory";
	public static final String PROPERTY_OBJECTS = "objects";
	public static final String SEPARATOR = ",";
	
	private NickiContext context;
	private Target target;
	
	protected TargetObjectFactory(NickiContext context, Target target) {
		super();
		this.context = context;
		this.target = target;
	}

	public DynamicObject getObject(ContextSearchResult rs) throws InstantiateDynamicObjectException {
		DynamicObject object =  _getObject(rs);
		object.setContext(context);
		return object;
	}

	
	
	private synchronized  DynamicObject _getObject(ContextSearchResult rs) throws InstantiateDynamicObjectException {
		String dn = rs.getNameInNamespace();
		for (Iterator<DynamicObject> iterator = target.getDynamicObjects().iterator(); iterator.hasNext();) {
			DynamicObject dynamicObject = iterator.next();
			if (dynamicObject.accept(rs)) {
				DynamicObject result = getExistingDynamicObject(dynamicObject, dn);
				if (result != null) {
					return result;
				}
			}
		}
		throw new InstantiateDynamicObjectException("Could not getObject " + dn);
	}

	public <T extends DynamicObject> T getObject(ContextSearchResult rs, Class<T> classDefinition) throws InstantiateDynamicObjectException, DynamicObjectException {
		T object =  _getObject(rs, classDefinition);
		object.setContext(context);
		return object;
	}

	
	
	private synchronized <T extends DynamicObject> T _getObject(ContextSearchResult rs, Class<T> classDefinition) throws InstantiateDynamicObjectException {
		String dn = rs.getNameInNamespace();
		for (@SuppressWarnings("unchecked")
		Iterator<T> iterator = (Iterator<T>) target.getDynamicObjects().iterator(); iterator.hasNext();) {
			T dynamicObject = iterator.next();
			if (classDefinition == null || classDefinition.isAssignableFrom(dynamicObject.getClass())) {
				if (dynamicObject.accept(rs)) {
					T result = getExistingDynamicObject(dynamicObject, dn);
					if (result != null) {
						return result;
					}
				}
			}
		}
		throw new InstantiateDynamicObjectException("Could not getObject " + dn);
	}

	public String getObjectClassFilter(Class<? extends DynamicObject> classDefinition) throws InstantiateDynamicObjectException {
		DynamicObject dynamicObject = findDynamicObject(classDefinition);
		return dynamicObject.getModel().getObjectClassFilter();
	}

	public String getNamingLdapAttribute(Class<? extends DynamicObject> classDefinition) throws InstantiateDynamicObjectException {
		DynamicObject dynamicObject = findDynamicObject(classDefinition);
		return dynamicObject.getModel().getNamingLdapAttribute();
	}

	@SuppressWarnings("unchecked")
	private <T extends DynamicObject> T findDynamicObject(Class<T> classDefinition) throws InstantiateDynamicObjectException {
		for (Iterator<DynamicObject> iterator = target.getDynamicObjects().iterator(); iterator.hasNext();) {
			DynamicObject dynamicObject = iterator.next();
			if (classDefinition.isAssignableFrom(dynamicObject.getClass())){
				return (T) dynamicObject;
			}
		}
		throw new InstantiateDynamicObjectException("Could not getObject " + classDefinition);
	}
	
	// TODO
	private DynamicObject findDynamicObject(String className) throws InstantiateDynamicObjectException {
		for (Iterator<DynamicObject> iterator = target.getDynamicObjects().iterator(); iterator.hasNext();) {
			DynamicObject dynamicObject = iterator.next();
			if (StringUtils.equals(dynamicObject.getClass().getName(), className)) {
				return dynamicObject;
			}
		}
		throw new InstantiateDynamicObjectException("Could not getObject " + className);
	}
	

	public <T extends DynamicObject> T getDynamicObject(Class<T> classDefinition) throws InstantiateDynamicObjectException {
		T storedDynamicObject = findDynamicObject(classDefinition);
		T dynamicObject = null;
		try {
			dynamicObject = getDynamicObject(storedDynamicObject);
			dynamicObject.setContext(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dynamicObject;
	}
	
	public DynamicObject getDynamicObject(String className) throws InstantiateDynamicObjectException {
		DynamicObject storedDynamicObject = findDynamicObject(className);
		DynamicObject dynamicObject = null;
		try {
			dynamicObject = getDynamicObject(storedDynamicObject);
			dynamicObject.setContext(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dynamicObject;
	}
	
	// TODO
	private <T extends DynamicObject> T getExistingDynamicObject(T pattern, String path) throws InstantiateDynamicObjectException {
		try {
			T object = getDynamicObject(pattern);
			object.initExisting(context, path);
			return object;
		} catch (Exception e) {
			throw new InstantiateDynamicObjectException(e);
		}
	}

	private <T extends DynamicObject> T getDynamicObject(T pattern) throws InstantiateDynamicObjectException {
		try {
			@SuppressWarnings("unchecked")
			T object = (T) pattern.getClass().newInstance();
			object.setModel(pattern.getModel());
			return object;
		} catch (Exception e) {
			throw new InstantiateDynamicObjectException(e);
		}
	}

	public <T extends DynamicObject> T getNewDynamicObject(Class<T> classDefinition, 
			String parentPath, String namingValue) throws InstantiateDynamicObjectException {
		try {
			T object = getDynamicObject(classDefinition);
			object.setContext(context);
			object.initNew(parentPath, namingValue);
			return object;
		} catch (Exception e) {
			throw new InstantiateDynamicObjectException(e);
		}
	}
	
	public  <T extends DynamicObject> T createNewDynamicObject(Class<T> classDefinition, 
			String parentPath, String namingValue) throws InstantiateDynamicObjectException {
		try {
			DynamicObject object = getNewDynamicObject(classDefinition, parentPath, namingValue);
			object.setContext(context);
			if (!context.isExist(object.getPath())) {
				object.create();
				return context.loadObject(classDefinition, object.getPath());
			}
			return null;
		} catch (Exception e) {
			throw new InstantiateDynamicObjectException(e);
		}
	}
	
}
