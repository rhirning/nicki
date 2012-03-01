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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.ldap.auth.NickiPrincipal;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.context.NickiContext.READONLY;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.ldap.objects.DynamicObjectExtension;

@SuppressWarnings("serial")
public class Target implements Serializable {

	private String targetName;
	private String propertyBase;
	private List<String> dynamicObjects = null;
	private Map<String, DynamicObject> dynamicObjectsMap = null;
	private Map<String, DynamicObjectExtension> extensionsMap = null;
	private Map<String, List<String>> extensions = null;

	public Map<String, DynamicObjectExtension> getExtensionsMap() {
		return extensionsMap;
	}

	public Target(String targetName, String propertyBase) {
		this.targetName = targetName;
		this.propertyBase = propertyBase;
	}

	public void setDynamicObjectsMap(Map<String, DynamicObject> initDynamicObjectsMap) {
		dynamicObjectsMap = initDynamicObjectsMap;
		
	}

	public void setExtensionsMap(Map<String, DynamicObjectExtension> initExtensionsMap) {
		extensionsMap = initExtensionsMap;
		
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
		sb.append(", ");
		for (String extensionName : extensionsMap.keySet()) {
			sb.append(" ").append(extensionName).append(":");
			sb.append(extensionsMap.get(extensionName).getClass().getSimpleName());
		}
		return sb.toString();
	}

	public DynamicObject login(NickiPrincipal principal) {
		try {
			return new SystemContext(this, null, READONLY.FALSE).login(principal.getName(), principal.getPassword());
		} catch (InvalidPrincipalException e) {
			return null;
		}
	}

	public NickiContext getNamedUserContext(DynamicObject user, String password) throws InvalidPrincipalException {
		return new NamedUserContext(this, user, password);
	}

	public NickiContext getGuestContext() {
		return new GuestContext(this, READONLY.TRUE);
	}

	public String getName() {
		return targetName;
	}

	public void setExtensions(Map<String, List<String>> extensions) {
		this.extensions = extensions;
	}

	public Map<String, List<String>> getExtensions() {
		return extensions;
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
	
	public <T extends DynamicObjectExtension> String getExtensionName(Class<T> extensionClass) throws DynamicObjectException {
		for (String name : getExtensionsMap().keySet()) {
			if (extensionClass == getExtensionsMap().get(name).getClass()) {
				return name;
			}
		}
		throw new DynamicObjectException("Invalid DynamicObject: " + extensionClass.getClass().getName());
	}
	
	public DynamicObject getDynamicObject(String dynamicObjectName) {
		return getDynamicObjectsMap().get(dynamicObjectName);
	}

	public DynamicObjectExtension getExtension(String extensionName) {
		return getExtensionsMap().get(extensionName);
	}

	@SuppressWarnings("unchecked")
	public <T extends DynamicObjectExtension> T getExtension(Class<T> extensionClass) {
		for (DynamicObjectExtension extension : getExtensionsMap().values()) {
			if (extension.getClass() == extensionClass) {
				return (T) extension;
			}
			
		}
		return null;
	}
	
	public <T extends DynamicObjectExtension> boolean hasExtension(DynamicObject dynamicObject, 
			Class<T> extensionClass) {
		try {
			return getExtensions().get(getDynamicObjectName(dynamicObject)).contains(getExtensionName(extensionClass));
		} catch (DynamicObjectException e) {
			return false;
		}
	}

}
