package org.mgnl.nicki.ldap.bean;

/*-
 * #%L
 * nicki-ldap
 * %%
 * Copyright (C) 2017 - 2024 Ralf Hirning
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

import org.mgnl.nicki.core.context.BeanQueryHandler.SCOPE;

/**
 * LDAP Handler connecting to IDVault 
 * @author rhi
 *
 */
public abstract class BasicLdapHandler {



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


	protected abstract SCOPE getScope();


}
