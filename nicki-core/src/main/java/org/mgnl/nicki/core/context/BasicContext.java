
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


import org.mgnl.nicki.core.auth.NickiPrincipal;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.objects.DataModel;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectAdapter;
import org.mgnl.nicki.core.objects.DynamicObjectException;



/**
 * The Class BasicContext.
 */
@SuppressWarnings("serial")
public abstract class BasicContext implements NickiContext {
	
	/** The Constant PROPERTY_BASE. */
	public static final String PROPERTY_BASE = "nicki.ldap.";
	
	/** The Constant TARGET_DEFAULT. */
	public static final String TARGET_DEFAULT = "edir";
	
	/** The target. */
	private Target target;
	
	/** The principal. */
	private NickiPrincipal principal;
	
	/** The user. */
	private DynamicObject user;
	
	/** The principal user. */
	private DynamicObject principalUser;
	
	/** The readonly. */
	private READONLY readonly;
	
	/** The adapter. */
	private DynamicObjectAdapter adapter; 

	/**
	 * Instantiates a new basic context.
	 *
	 * @param adapter the adapter
	 * @param target the target
	 * @param readonly the readonly
	 */
	protected BasicContext(DynamicObjectAdapter adapter, Target target, READONLY readonly) {
		this.adapter = adapter;
		this.target = target;
		this.readonly = readonly;
	}	

	/**
	 * Load object as.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @param dynamicObject the dynamic object
	 * @return the t
	 */
	public <T extends DynamicObject> T loadObjectAs(Class<T> classDefinition,
			DynamicObject dynamicObject) {
		return loadObject(classDefinition, dynamicObject.getPath());
	}
	
	/**
	 * Creates the dynamic object.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @param parentPath the parent path
	 * @param namingValue the naming value
	 * @return the t
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 * @throws DynamicObjectException the dynamic object exception
	 */
	public <T extends DynamicObject> T createDynamicObject(Class<T> classDefinition, String parentPath, String namingValue)
			throws  InstantiateDynamicObjectException, DynamicObjectException {
		if (this.readonly == READONLY.TRUE) {
			throw new DynamicObjectException("READONLY: could not create object: " + namingValue);
		}
		return getObjectFactory().createNewDynamicObject(classDefinition, parentPath, namingValue);
	}

	/**
	 * Gets the target.
	 *
	 * @return the target
	 */
	public Target getTarget() {
		return target;
	}
	
	/**
	 * Gets the object class filter.
	 *
	 * @param nickiContext the nicki context
	 * @param classDefinition the class definition
	 * @return the object class filter
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	public String getObjectClassFilter(NickiContext nickiContext, Class<? extends DynamicObject> classDefinition) throws InstantiateDynamicObjectException {
		return getObjectFactory().getObjectClassFilter(nickiContext, classDefinition);
	}

	/**
	 * Gets the principal.
	 *
	 * @return the principal
	 * @throws DynamicObjectException the dynamic object exception
	 */
	public NickiPrincipal getPrincipal() throws DynamicObjectException {
		return this.principal;
	}

	/**
	 * Sets the principal.
	 *
	 * @param principal the new principal
	 */
	protected void setPrincipal(NickiPrincipal principal) {
		this.principal = principal;
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public DynamicObject getUser() {
		return user;
	}

	/**
	 * Sets the user.
	 *
	 * @param user the new user
	 */
	public void setUser(DynamicObject user) {
		this.user = user;
		if (this.principalUser == null) {
			this.principalUser = user;
		}
	}

	/**
	 * Checks if is readonly.
	 *
	 * @return true, if is readonly
	 */
	public boolean isReadonly() {
		return readonly == READONLY.TRUE;
	}

	/**
	 * Sets the readonly.
	 *
	 * @param readonly the new readonly
	 */
	public void setReadonly(boolean readonly) {
		this.readonly = readonly?READONLY.TRUE:READONLY.FALSE;
	}

	/**
	 * Gets the adapter.
	 *
	 * @return the adapter
	 */
	public DynamicObjectAdapter getAdapter() {
		return adapter;
	}

	/**
	 * Sets the adapter.
	 *
	 * @param adapter the new adapter
	 */
	public void setAdapter(DynamicObjectAdapter adapter) {
		this.adapter = adapter;
	}

	/**
	 * Gets the principal user.
	 *
	 * @return the principal user
	 */
	public DynamicObject getPrincipalUser() {
		return principalUser;
	}

	/**
	 * Sets the principal user.
	 *
	 * @param principalUser the new principal user
	 */
	public void setPrincipalUser(DynamicObject principalUser) {
		this.principalUser = principalUser;
		if (principalUser == null) {
			this.principalUser = this.user;
		}
	}

	/**
	 * Gets the data model.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the class definition
	 * @return the data model
	 * @throws InstantiateDynamicObjectException the instantiate dynamic object exception
	 */
	@Override
	public <T extends DynamicObject> DataModel getDataModel(
			Class<T> classDefinition) throws InstantiateDynamicObjectException {
		return getTarget().getDataModel(classDefinition);
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		String userName;
		try {
			userName = getPrincipal().getName();
		} catch (DynamicObjectException e) {
			userName = "unknown";
		}
		return getTarget().getName() + " - " + getClass().getSimpleName() + " - " + userName;
	}
	
}
