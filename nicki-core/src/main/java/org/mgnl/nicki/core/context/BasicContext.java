/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
