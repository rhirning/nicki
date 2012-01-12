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
