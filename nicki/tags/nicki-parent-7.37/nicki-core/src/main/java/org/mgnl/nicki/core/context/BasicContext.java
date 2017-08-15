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

import org.mgnl.nicki.core.auth.NickiPrincipal;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.objects.DataModel;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectAdapter;
import org.mgnl.nicki.core.objects.DynamicObjectException;


@SuppressWarnings("serial")
public abstract class BasicContext implements NickiContext {
	public static final String PROPERTY_BASE = "nicki.ldap.";
	public static final String TARGET_DEFAULT = "edir";
	private Target target;
	private NickiPrincipal principal;
	private DynamicObject user;
	private DynamicObject principalUser;
	private READONLY readonly;
	private DynamicObjectAdapter adapter; 

	protected BasicContext(DynamicObjectAdapter adapter, Target target, READONLY readonly) {
		this.adapter = adapter;
		this.target = target;
		this.readonly = readonly;
	}	

	public <T extends DynamicObject> T loadObjectAs(Class<T> classDefinition,
			DynamicObject dynamicObject) {
		return loadObject(classDefinition, dynamicObject.getPath());
	}
	
	public <T extends DynamicObject> T createDynamicObject(Class<T> classDefinition, String parentPath, String namingValue)
			throws  InstantiateDynamicObjectException, DynamicObjectException {
		if (this.readonly == READONLY.TRUE) {
			throw new DynamicObjectException("READONLY: could not create object: " + namingValue);
		}
		return getObjectFactory().createNewDynamicObject(classDefinition, parentPath, namingValue);
	}

	public Target getTarget() {
		return target;
	}
	
	public String getObjectClassFilter(NickiContext nickiContext, Class<? extends DynamicObject> classDefinition) throws InstantiateDynamicObjectException {
		return getObjectFactory().getObjectClassFilter(nickiContext, classDefinition);
	}

	public NickiPrincipal getPrincipal() throws DynamicObjectException {
		return this.principal;
	}

	protected void setPrincipal(NickiPrincipal principal) {
		this.principal = principal;
	}

	public DynamicObject getUser() {
		return user;
	}

	public void setUser(DynamicObject user) {
		this.user = user;
		if (this.principalUser == null) {
			this.principalUser = user;
		}
	}

	public boolean isReadonly() {
		return readonly == READONLY.TRUE;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly?READONLY.TRUE:READONLY.FALSE;
	}

	public DynamicObjectAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(DynamicObjectAdapter adapter) {
		this.adapter = adapter;
	}

	public DynamicObject getPrincipalUser() {
		return principalUser;
	}

	public void setPrincipalUser(DynamicObject principalUser) {
		this.principalUser = principalUser;
		if (principalUser == null) {
			this.principalUser = this.user;
		}
	}

	@Override
	public <T extends DynamicObject> DataModel getDataModel(
			Class<T> classDefinition) throws InstantiateDynamicObjectException {
		return getTarget().getDataModel(classDefinition);
	}
	
}
