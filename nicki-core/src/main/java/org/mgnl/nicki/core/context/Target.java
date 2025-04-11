
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
import org.mgnl.nicki.core.util.Classes;

import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class Target.
 */
@Slf4j
@SuppressWarnings("serial")
public class Target implements Serializable {

	/** The target name. */
	private String targetName;
	
	/** The property base. */
	private String propertyBase;
	
	/** The security authentication. */
	private String securityAuthentication;
	
	/** The dynamic objects. */
	private List<String> dynamicObjects;
	
	/** The all dynamic objects map. */
	private Map<Class<? extends DynamicObject>, DynamicObject> allDynamicObjectsMap = new HashMap<Class<? extends DynamicObject>, DynamicObject>();
	
	/** The dynamic objects map. */
	private Map<String, DynamicObject> dynamicObjectsMap;
	
	/** The context factory. */
	private ContextFactory contextFactory;
	
	/** The base dn. */
	private String baseDn;

	/**
	 * Instantiates a new target.
	 *
	 * @param targetName the target name
	 * @param propertyBase the property base
	 */
	public Target(String targetName, String propertyBase) {
		this.targetName = targetName;
		this.propertyBase = propertyBase;
		this.securityAuthentication = getProperty("securityAuthentication", "GSSAPI");
		this.baseDn = getProperty("baseDn", Config.getString("nicki.users.basedn"));
	}

	/**
	 * Sets the dynamic objects map.
	 *
	 * @param initDynamicObjectsMap the init dynamic objects map
	 */
	public void setDynamicObjectsMap(Map<String, DynamicObject> initDynamicObjectsMap) {
		dynamicObjectsMap = initDynamicObjectsMap;
		
	}

	/**
	 * Gets the dynamic objects map.
	 *
	 * @return the dynamic objects map
	 */
	public Map<String, DynamicObject> getDynamicObjectsMap() {
		return dynamicObjectsMap;
	}
	
	/**
	 * Gets the property.
	 *
	 * @param appendix the appendix
	 * @return the property
	 */
	public String getProperty(String appendix) {
		return Config.getString(propertyBase + "." + appendix);
	}
	
	/**
	 * Gets the boolean.
	 *
	 * @param appendix the appendix
	 * @return the boolean
	 */
	public boolean getBoolean(String appendix) {
		return Config.getBoolean(propertyBase + "." + appendix);
	}
	
	/**
	 * Gets the property.
	 *
	 * @param appendix the appendix
	 * @param defaultValue the default value
	 * @return the property
	 */
	public String getProperty(String appendix, String defaultValue) {
		return Config.getString(propertyBase + "." + appendix, defaultValue);
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
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

	/**
	 * Login.
	 *
	 * @param principal the principal
	 * @return the dynamic object
	 */
	public DynamicObject login(NickiPrincipal principal) {
		try {
			return getSystemContext(null).login(principal.getName(), principal.getPassword());
		} catch (InvalidPrincipalException e) {
			return null;
		}
	}

	/**
	 * Gets the system context.
	 *
	 * @param user the user
	 * @return the system context
	 * @throws InvalidPrincipalException the invalid principal exception
	 */
	public NickiContext getSystemContext(DynamicObject user) throws InvalidPrincipalException {
		return getContextFactory().getSystemContext(this, user);
	}

	/**
	 * Gets the named user context.
	 *
	 * @param user the user
	 * @param password the password
	 * @return the named user context
	 * @throws InvalidPrincipalException the invalid principal exception
	 */
	public NickiContext getNamedUserContext(DynamicObject user, String password) throws InvalidPrincipalException {
		return getContextFactory().getNamedUserContext(this, user, password);
	}

	/**
	 * Gets the guest context.
	 *
	 * @return the guest context
	 */
	public NickiContext getGuestContext() {
		return getContextFactory().getGuestContext(this);
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return targetName;
	}

	/**
	 * Sets the dynamic objects.
	 *
	 * @param dynamicObjects the new dynamic objects
	 */
	public void setDynamicObjects(List<String> dynamicObjects) {
		this.dynamicObjects = dynamicObjects;
	}

	/**
	 * Gets the dynamic objects.
	 *
	 * @return the dynamic objects
	 */
	public List<String> getDynamicObjects() {
		return dynamicObjects;
	}
	
	/**
	 * Gets the dynamic object name.
	 *
	 * @param dynamicObject the dynamic object
	 * @return the dynamic object name
	 * @throws DynamicObjectException the dynamic object exception
	 */
	public String getDynamicObjectName(DynamicObject dynamicObject) throws DynamicObjectException {
		for (String name : getDynamicObjectsMap().keySet()) {
			if (dynamicObject.getClass() == getDynamicObjectsMap().get(name).getClass()) {
				return name;
			}
		}
		throw new DynamicObjectException("Invalid DynamicObject: " + dynamicObject.getClass().getName());
	}
	
	/**
	 * Gets the dynamic object.
	 *
	 * @param dynamicObjectName the dynamic object name
	 * @return the dynamic object
	 */
	public DynamicObject getDynamicObject(String dynamicObjectName) {
		return getDynamicObjectsMap().get(dynamicObjectName);
	}

	/**
	 * Gets the dynamic object.
	 *
	 * @param classDefinition the class definition
	 * @return the dynamic object
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	public DynamicObject getDynamicObject(Class<? extends DynamicObject> classDefinition) throws InstantiateDynamicObjectException {
		DynamicObject dynamicObject = allDynamicObjectsMap.get(classDefinition);
		if (dynamicObject == null) {
			// 
			try {
				return findDynamicObject(classDefinition);
			} catch (Exception e1) {
				log.debug(classDefinition.getName() + " not found, so create a new one");
			}
			try {
				dynamicObject = Classes.newInstance(classDefinition);
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

	/**
	 * Find dynamic object.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @return the t
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
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

	/**
	 * Gets the context factory.
	 *
	 * @return the context factory
	 */
	public ContextFactory getContextFactory() {
		return contextFactory;
	}

	/**
	 * Sets the context factory.
	 *
	 * @param contextFactory the new context factory
	 */
	public void setContextFactory(ContextFactory contextFactory) {
		this.contextFactory = contextFactory;
	}
	
	/**
	 * Gets the data model.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @return the data model
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	public <T extends DynamicObject> DataModel getDataModel(
			Class<T> classDefinition) throws InstantiateDynamicObjectException {
		try {
			return getDynamicObject(classDefinition).getModel();
		} catch (Exception e) {
			throw new InstantiateDynamicObjectException(e);
		}

	}

	/**
	 * Gets the security authentication.
	 *
	 * @return the security authentication
	 */
	public String getSecurityAuthentication() {
		return securityAuthentication;
	}

	/**
	 * Gets the base dn.
	 *
	 * @return the base dn
	 */
	public String getBaseDn() {
		return baseDn;
	}
}
