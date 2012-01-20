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
package org.mgnl.nicki.ldap.data;

import java.util.List;

import javax.naming.directory.SearchControls;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.context.QueryHandler;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

public class InitialObjectLdapQueryHandler extends ObjectLoaderLdapQueryHandler implements QueryHandler {
	
	public InitialObjectLdapQueryHandler(NickiContext context, String path) {
		super(context, path);
	}

	@Override
	public void handle(List<ContextSearchResult> results) throws DynamicObjectException {
		if (results != null && results.size() > 0) {
			try {
				dynamicObject = getContext().getObjectFactory().getObject(results.get(0));
				dynamicObject.initExisting(getContext(), getBaseDN());
			} catch (InstantiateDynamicObjectException e) {
				throw new DynamicObjectException(e);
			}
		}
	}

	public SearchControls getConstraints() {
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.OBJECT_SCOPE);
		// Specify the ids of the attributes to return
		String[] attrIDs = { "objectClass" };
		constraints.setReturningAttributes(attrIDs);
		return constraints;
	}

}
