/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
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
 */
package org.mgnl.nicki.ldap.context;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.ldap.auth.NickiPrincipal;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.context.NickiContext.READONLY;
import org.mgnl.nicki.ldap.objects.DynamicObject;

@SuppressWarnings("serial")
public class Target implements Serializable {

	private String targetName;
	private String propertyBase;
	private List<DynamicObject> dynamicObjects = null;

	public Target(String targetName, String propertyBase) {
		this.targetName = targetName;
		this.propertyBase = propertyBase;
	}

	public void setDynamicObjects(List<DynamicObject> initDynamicObjects) {
		dynamicObjects = initDynamicObjects;
		
	}

	public List<DynamicObject> getDynamicObjects() {
		return dynamicObjects;
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
		for (Iterator<DynamicObject> iterator = dynamicObjects.iterator(); iterator.hasNext();) {
			sb.append(" ");
			sb.append(iterator.next().getClass().getSimpleName());
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
}
