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
package org.mgnl.nicki.core.context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.auth.NickiPrincipal;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;

@SuppressWarnings("serial")
public class Target implements Serializable {

	private String targetName;
	private String propertyBase;
	private List<String> dynamicObjects = null;
	private Map<Class<? extends DynamicObject>, DynamicObject> allDynamicObjectsMap = new HashMap<Class<? extends DynamicObject>, DynamicObject>();
	private Map<String, DynamicObject> dynamicObjectsMap = null;
	private ContextFactory contextFactory = null;

	public Target(String targetName, String propertyBase) {
		this.targetName = targetName;
		this.propertyBase = propertyBase;
	}

	public void setDynamicObjectsMap(Map<String, DynamicObject> initDynamicObjectsMap) {
		dynamicObjectsMap = initDynamicObjectsMap;
		
	}

	public Map<String, DynamicObject> getDynamicObjectsMap() {
		return dynamicObjectsMap;
	}
	
	public String getProperty(String appendix) {
		return Config.getProperty(propertyBase + "." + appendix);
	}

	public ObjectFactory getObjectFactory(NickiContext context) {
		return new TargetObjectFactory(context, this);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Target [").append(targetName).append("]:");
		for (String dynamicObjectName : dynamicObjectsMap.keySet()) {
			sb.append(" ").append(dynamicObjectName).append(":");
			sb.append(dynamicObjectsMap.get(dynamicObjectName).getClass().getSimpleName());
		}
		return sb.toString();
	}

	public DynamicObject login(NickiPrincipal principal) {
		try {
			return getSystemContext(null).login(principal.getName(), principal.getPassword());
		} catch (InvalidPrincipalException e) {
			return null;
		}
	}

	public NickiContext getSystemContext(DynamicObject user) throws InvalidPrincipalException {
		return getContextFactory().getSystemContext(this, user);
	}

	public NickiContext getNamedUserContext(DynamicObject user, String password) throws InvalidPrincipalException {
		return getContextFactory().getNamedUserContext(this, user, password);
	}

	public NickiContext getGuestContext() {
		return getContextFactory().getGuestContext(this);
	}

	public String getName() {
		return targetName;
	}

	public void setDynamicObjects(List<String> dynamicObjects) {
		this.dynamicObjects = dynamicObjects;
	}

	public List<String> getDynamicObjects() {
		return dynamicObjects;
	}
	
	public String getDynamicObjectName(DynamicObject dynamicObject) throws DynamicObjectException {
		for (String name : getDynamicObjectsMap().keySet()) {
			if (dynamicObject.getClass() == getDynamicObjectsMap().get(name).getClass()) {
				return name;
			}
		}
		throw new DynamicObjectException("Invalid DynamicObject: " + dynamicObject.getClass().getName());
	}
	
	public DynamicObject getDynamicObject(String dynamicObjectName) {
		return getDynamicObjectsMap().get(dynamicObjectName);
	}

	public DynamicObject getDynamicObject(Class<? extends DynamicObject> classDefinition) throws InstantiateDynamicObjectException {
		DynamicObject dynamicObject = allDynamicObjectsMap.get(classDefinition);
		if (dynamicObject == null) {
			try {
				dynamicObject = classDefinition.newInstance();
				dynamicObject.initDataModel();
				allDynamicObjectsMap.put(classDefinition, dynamicObject);
				return dynamicObject;
			} catch (Exception e) {
				throw new InstantiateDynamicObjectException(e);
			}
		} else {
			return dynamicObject;
		}
	}

	public ContextFactory getContextFactory() {
		return contextFactory;
	}

	public void setContextFactory(ContextFactory contextFactory) {
		this.contextFactory = contextFactory;
	}

}
