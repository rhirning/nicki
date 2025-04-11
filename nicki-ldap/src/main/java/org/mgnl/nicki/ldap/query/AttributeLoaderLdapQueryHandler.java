
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


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.SearchControls;

import org.mgnl.nicki.core.data.QueryHandler;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.ldap.context.LdapContext;

// TODO: Auto-generated Javadoc
/**
 * The Class AttributeLoaderLdapQueryHandler.
 */
public class AttributeLoaderLdapQueryHandler extends BasicLdapHandler implements QueryHandler {

	/** The dynamic object. */
	private DynamicObject dynamicObject;
	
	/** The attributes. */
	private String[] attributes;
	
	/** The lists. */
	private Map<String, List<Object>> lists = new HashMap<String, List<Object>>();
	
	/**
	 * Instantiates a new attribute loader ldap query handler.
	 *
	 * @param dynamicObject the dynamic object
	 * @param attributes the attributes
	 */
	public AttributeLoaderLdapQueryHandler(DynamicObject dynamicObject, String[] attributes) {
		super((LdapContext) dynamicObject.getContext());
		this.dynamicObject = dynamicObject;
		this.attributes = attributes;
	}

	/**
	 * Handle.
	 *
	 * @param results the results
	 * @throws DynamicObjectException the dynamic object exception
	 */
	public void handle(List<ContextSearchResult> results) throws DynamicObjectException {
		for (ContextSearchResult rs : results) {
			for(int i = 0; i < attributes.length; i++) {
				lists.put(attributes[i], rs.getValues(attributes[i]));
			}
		}
	}

	/**
	 * Gets the constraints.
	 *
	 * @return the constraints
	 */
	public SearchControls getConstraints() {
		SearchControls constraints = super.getConstraints();
		// Specify the ids of the attributes to return
		constraints.setReturningAttributes(attributes);
		return constraints;
	}

	/**
	 * Gets the base DN.
	 *
	 * @return the base DN
	 */
	public String getBaseDN() {
		return this.dynamicObject.getPath();
	}

	/**
	 * Gets the filter.
	 *
	 * @return the filter
	 */
	public String getFilter() {
		return null;
	}

	/**
	 * Gets the lists.
	 *
	 * @return the lists
	 */
	public Map<String, List<Object>> getLists() {
		return lists;
	}

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	@Override
	public SCOPE getScope() {
		return SCOPE.OBJECT;
	}
}
