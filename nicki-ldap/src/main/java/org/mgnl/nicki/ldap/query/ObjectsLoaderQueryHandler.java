
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


import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.core.data.Query;

import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class ObjectsLoaderQueryHandler.
 */
@Slf4j
public class ObjectsLoaderQueryHandler extends ObjectLoaderLdapQueryHandler {

	/** The list. */
	private List<DynamicObject> list = new ArrayList<DynamicObject>();
	
	/**
	 * Gets the list.
	 *
	 * @return the list
	 */
	public List<DynamicObject> getList() {
		return list;
	}

	/**
	 * Instantiates a new objects loader query handler.
	 *
	 * @param context the context
	 * @param dn the dn
	 * @param filter the filter
	 */
	public ObjectsLoaderQueryHandler(NickiContext context, String dn, String filter) {
		super(context, dn);
		setFilter(filter);
	}

	/**
	 * Instantiates a new objects loader query handler.
	 *
	 * @param context the context
	 * @param query the query
	 */
	public ObjectsLoaderQueryHandler(NickiContext context, Query query) {
		super(context, query.getBaseDN());
		setFilter(query.getFilter());
	}

	/**
	 * Handle.
	 *
	 * @param results the results
	 * @throws DynamicObjectException the dynamic object exception
	 */
	@Override
	public void handle(List<ContextSearchResult> results) throws DynamicObjectException {
		for (ContextSearchResult rs : results) {
			DynamicObject dynamicObject = null;
			if (getClassDefinition() != null) {
				try {
					dynamicObject = getContext().getObjectFactory().getObject(rs, getClassDefinition());
				} catch (InstantiateDynamicObjectException e) {
					dynamicObject = null;
					log.debug(e.getMessage());
				}
			} else {
				try {
					dynamicObject = getContext().getObjectFactory().getObject(rs);
				} catch (InstantiateDynamicObjectException e) {
					dynamicObject = null;
					throw new DynamicObjectException(e);
				}
			}
			if (dynamicObject != null) {
				list.add(dynamicObject);
			}
		}
	}
	


	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	@Override
	public SCOPE getScope() {
		return SCOPE.SUBTREE;
	}

	/**
	 * Gets the constraints.
	 *
	 * @return the constraints
	 */
	@Override
	public SearchControls getConstraints() {
		SearchControls constraints =super.getConstraints();
		String[] attrIDs = { "objectClass" };
		constraints.setReturningAttributes(attrIDs);

		return constraints;
	}
}
