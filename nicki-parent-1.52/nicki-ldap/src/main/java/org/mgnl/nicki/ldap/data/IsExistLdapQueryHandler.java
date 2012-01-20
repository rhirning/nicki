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
import org.mgnl.nicki.ldap.core.BasicLdapHandler;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;


public class IsExistLdapQueryHandler extends BasicLdapHandler implements QueryHandler {
	
	private String dn = null;

	private boolean exist = false;

	public IsExistLdapQueryHandler(NickiContext context, String path) {
		super(context);
		this.dn = path;
	}


	public String getBaseDN() {
		return this.dn;
	}

	public void handle(List<ContextSearchResult> results) {
		try {
			if (results != null && results.size() > 0) {
				exist = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SearchControls getConstraints() {
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.OBJECT_SCOPE);
		return constraints;
	}


	public boolean isExist() {
		return exist;
	}
}
