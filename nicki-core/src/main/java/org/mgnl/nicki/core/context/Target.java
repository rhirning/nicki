
package org.mgnl.nicki.core.context;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.auth.NickiPrincipal;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.objects.DataModel;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class Target implements Serializable {
	private static final Logger LOG = LoggerFactory.getLogger(Target.class);

	private String targetName;
	private String propertyBase;
	private String securityAuthentication;
	private List<String> dynamicObjects;
	private Map<Class<? extends DynamicObject>, DynamicObject> allDynamicObjectsMap = new HashMap<Class<? extends DynamicObject>, DynamicObject>();
	private Map<String, DynamicObject> dynamicObjectsMap;
	private ContextFactory contextFactory;

	public Target(String targetName, String propertyBase) {
		this.targetName = targetName;
		this.propertyBase = propertyBase;
		this.securityAuthentication = getProperty("securityAuthentication", "GSSAPI");
	}

	public void setDynamicObjectsMap(Map<String, DynamicObject> initDynamicObjectsMap) {
		dynamicObjectsMap = initDynamicObjectsMap;
		
	}

	public Map<String, DynamicObject> getDynamicObjectsMap() {
		return dynamicObjectsMap;
	}
	
	public String getProperty(String appendix) {
		return Config.getString(propertyBase + "." + appendix);
	}
	
	public String getProperty(String appendix, String defaultValue) {
		return Config.getString(propertyBase + "." + appendix, defaultValue);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
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
			// 
			try {
				return findDynamicObject(classDefinition);
			} catch (Exception e1) {
				LOG.debug(classDefinition.getName() + " not found, so create a new one");
			}
			try {
				dynamicObject = classDefinition.newInstance();
				TargetFactory.initDataModel(dynamicObject);
				allDynamicObjectsMap.put(classDefinition, dynamicObject);
				return dynamicObject;
			} catch (Exception e) {
				throw new InstantiateDynamicObjectException(e);
			}
		} else {
			return dynamicObject;
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends DynamicObject> T findDynamicObject(Class<T> classDefinition) throws InstantiateDynamicObjectException {
		for (String dynamicObjectName : getDynamicObjects()) {
			DynamicObject dynamicObject = getDynamicObject(dynamicObjectName);
			if (classDefinition.isAssignableFrom(dynamicObject.getClass())){
				return (T) dynamicObject;
			}
		}
		throw new InstantiateDynamicObjectException("Could not getObject " + classDefinition);
	}

	public ContextFactory getContextFactory() {
		return contextFactory;
	}

	public void setContextFactory(ContextFactory contextFactory) {
		this.contextFactory = contextFactory;
	}
	
	public <T extends DynamicObject> DataModel getDataModel(
			Class<T> classDefinition) throws InstantiateDynamicObjectException {
		try {
			return getDynamicObject(classDefinition).getModel();
		} catch (Exception e) {
			throw new InstantiateDynamicObjectException(e);
		}

	}

	public String getSecurityAuthentication() {
		return securityAuthentication;
	}
}
