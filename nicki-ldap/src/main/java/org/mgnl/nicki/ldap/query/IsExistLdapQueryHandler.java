
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
import org.mgnl.nicki.core.data.QueryHandler;
import org.mgnl.nicki.core.objects.ContextSearchResult;

import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class IsExistLdapQueryHandler.
 */
@Slf4j
public class IsExistLdapQueryHandler extends BasicLdapHandler implements QueryHandler {
	
	/** The dn. */
	private String dn;

	/** The exist. */
	private boolean exist = false;

	/**
	 * Instantiates a new checks if is exist ldap query handler.
	 *
	 * @param context the context
	 * @param path the path
	 */
	public IsExistLdapQueryHandler(NickiContext context, String path) {
		super(context);
		this.dn = path;
	}


	/**
	 * Gets the base DN.
	 *
	 * @return the base DN
	 */
	public String getBaseDN() {
		return this.dn;
	}

	/**
	 * Handle.
	 *
	 * @param results the results
	 */
	public void handle(List<ContextSearchResult> results) {
		try {
			if (results != null && results.size() > 0) {
				exist = true;
			}
		} catch (Exception e) {
			log.error("Error", e);
		}
	}

	/**
	 * Checks if is exist.
	 *
	 * @return true, if is exist
	 */
	public boolean isExist() {
		return exist;
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
