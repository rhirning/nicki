
package org.mgnl.nicki.ldap.query;

/*-
 * #%L
 * nicki-ldap
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


import javax.naming.directory.SearchControls;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.data.QueryHandler;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.helper.LdapHelper.LOGIC;

import lombok.extern.slf4j.Slf4j;


/**
 * The Class BasicLdapHandler.
 */
@Slf4j
public abstract class BasicLdapHandler implements QueryHandler {
	
	/** The context. */
	private NickiContext context;
	
	/** The class definition. */
	private Class<? extends DynamicObject> classDefinition;
	
	/** The filter. */
	private String filter;


	/**
	 * Instantiates a new basic ldap handler.
	 *
	 * @param context the context
	 */
	public BasicLdapHandler(NickiContext context) {
		super();
		this.context = context;
	}

	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	public NickiContext getContext() {
		return context;
	}

	/**
	 * Sets the class definition.
	 *
	 * @param <T> the generic type
	 * @param classDefinition the new class definition
	 */
	public <T extends DynamicObject> void setClassDefinition(Class<T> classDefinition) {
		try {
			this.classDefinition = findDynamicObject(classDefinition).getClass();
		} catch (InstantiateDynamicObjectException e) {
			this.classDefinition = classDefinition;
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
		for (String dynamicObjectName : getContext().getTarget().getDynamicObjects()) {
			DynamicObject dynamicObject = getContext().getTarget().getDynamicObject(dynamicObjectName);
			if (classDefinition.isAssignableFrom(dynamicObject.getClass())){
				return (T) dynamicObject;
			}
		}
		throw new InstantiateDynamicObjectException("Could not getObject " + classDefinition);
	}

	/**
	 * Gets the class definition.
	 *
	 * @return the class definition
	 */
	public Class<? extends DynamicObject> getClassDefinition() {
		return classDefinition;
	}
	
	/**
	 * Gets the filter.
	 *
	 * @return the filter
	 */
	public String getFilter() {
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotBlank(filter)) {
			LdapHelper.addQuery(sb, filter, LOGIC.AND);
		}
		
		if (getClassDefinition() == null) {
			if (sb.length() == 0) {
				LdapHelper.addQuery(sb, "objectClass=*", LOGIC.AND);
			}
		} else {
			try {
				LdapHelper.addQuery(sb, getContext().getObjectClassFilter(getContext(), getClassDefinition()), LOGIC.AND);
			} catch (InstantiateDynamicObjectException e) {
				log.error("Error", e);
			}
		}
		return sb.toString();
	}

	/**
	 * Sets the filter.
	 *
	 * @param filter the new filter
	 */
	public void setFilter(String filter) {
		this.filter = filter;
	}

	/**
	 * Gets the constraints.
	 *
	 * @return the constraints
	 */
	public SearchControls getConstraints() {
		SearchControls constraints = new SearchControls();
		if (getScope() == SCOPE.OBJECT) {
			constraints.setSearchScope(SearchControls.OBJECT_SCOPE);
		} else if (getScope() == SCOPE.ONELEVEL) {
			constraints.setSearchScope(SearchControls.ONELEVEL_SCOPE);
		} else if (getScope() == SCOPE.SUBTREE) {
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		}
		return constraints;
	}

	/**
	 * Gets the page size.
	 *
	 * @return the page size
	 */
	@Override
	public int getPageSize() {
		return 0;
	}

	/**
	 * One page.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean onePage() {
		return false;
	}




}
