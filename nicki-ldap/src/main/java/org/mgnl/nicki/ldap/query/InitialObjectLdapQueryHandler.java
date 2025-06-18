
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


import java.util.List;

import javax.naming.directory.SearchControls;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.QueryHandler;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObjectException;


/**
 * The Class InitialObjectLdapQueryHandler.
 */
public class InitialObjectLdapQueryHandler extends ObjectLoaderLdapQueryHandler implements QueryHandler {
	
	/**
	 * Instantiates a new initial object ldap query handler.
	 *
	 * @param context the context
	 * @param path the path
	 */
	public InitialObjectLdapQueryHandler(NickiContext context, String path) {
		super(context, path);
	}

	/**
	 * Handle.
	 *
	 * @param results the results
	 * @throws DynamicObjectException the dynamic object exception
	 */
	@Override
	public void handle(List<ContextSearchResult> results) throws DynamicObjectException {
		if (results != null && results.size() > 0) {
			try {
				dynamicObject = getContext().getObjectFactory().getObject(results.get(0));
				getContext().getAdapter().initExisting(dynamicObject, getContext(), getBaseDN());
			} catch (Exception e) {
				throw new DynamicObjectException(e);
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
		String[] attrIDs = { "objectClass" };
		constraints.setReturningAttributes(attrIDs);
		return constraints;
	}

}
