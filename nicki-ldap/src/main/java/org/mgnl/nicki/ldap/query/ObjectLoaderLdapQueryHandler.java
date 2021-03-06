
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

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.data.QueryHandler;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObjectLoaderLdapQueryHandler extends BasicLdapHandler implements QueryHandler {
	
	private String dn;
	protected DynamicObject dynamicObject;

	public DynamicObject getDynamicObject() {
		return dynamicObject;
	}

	public ObjectLoaderLdapQueryHandler(DynamicObject dynamicObject) {
		super(dynamicObject.getContext());
		this.dynamicObject = dynamicObject;
		this.dn = dynamicObject.getPath();
	}


	public ObjectLoaderLdapQueryHandler(NickiContext context, String dn) {
		super(context);
		this.dynamicObject = null;
		this.dn = dn;
	}


	public String getBaseDN() {
		return this.dn;
	}

	public void handle(List<ContextSearchResult> results) throws DynamicObjectException {
		try {
			if (results != null && results.size() > 0) {
				if (this.dynamicObject == null) {
					try {
						if (getClassDefinition() != null) {
							dynamicObject = getContext().getObjectFactory().getObject(results.get(0), getClassDefinition());
						} else {
							dynamicObject = getContext().getObjectFactory().getObject(results.get(0));
						}
					} catch (InstantiateDynamicObjectException e) {
						throw new DynamicObjectException(e);
					}
					getContext().getAdapter().initExisting(dynamicObject, getContext(), getBaseDN());
				}
				this.dynamicObject.init(results.get(0));
			}
		} catch (Exception e) {
			log.error("Error", e);
		}
	}

	@Override
	public SCOPE getScope() {
		return SCOPE.OBJECT;
	}

}
