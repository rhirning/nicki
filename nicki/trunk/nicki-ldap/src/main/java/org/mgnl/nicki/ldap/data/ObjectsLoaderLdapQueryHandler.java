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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.core.LdapQuery;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

public class ObjectsLoaderLdapQueryHandler extends ObjectLoaderLdapQueryHandler {

	private List<DynamicObject> list = new ArrayList<DynamicObject>();
	
	public List<DynamicObject> getList() {
		return list;
	}

	public ObjectsLoaderLdapQueryHandler(NickiContext context, String dn, String filter) {
		super(context, dn);
		setFilter(filter);
	}

	public ObjectsLoaderLdapQueryHandler(NickiContext context, LdapQuery query) {
		super(context, query.getBaseDN());
		setFilter(query.getFilter());
	}

	@Override
	public void handle(List<ContextSearchResult> results) throws DynamicObjectException {
		for (Iterator<ContextSearchResult> iterator = results.iterator(); iterator.hasNext();) {
			if (getClassDefinition() != null) {
				try {
					ContextSearchResult rs = iterator.next();
					DynamicObject dynamicObject = getContext().getObjectFactory().getObject(rs, getClassDefinition());
					list.add(dynamicObject);
				} catch (InstantiateDynamicObjectException e) {
					System.out.println(e.getMessage());
				}
			} else {
				try {
					list.add(getContext().getObjectFactory().getObject(iterator.next()));
				} catch (InstantiateDynamicObjectException e) {
					throw new DynamicObjectException(e);
				}
			}
		}
	}

	@Override
	public SearchControls getConstraints() {
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		// Specify the ids of the attributes to return
		String[] attrIDs = { "objectClass" };
		constraints.setReturningAttributes(attrIDs);

		return constraints;
	}
}
