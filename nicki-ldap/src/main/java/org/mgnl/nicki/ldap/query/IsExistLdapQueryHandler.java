/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.ldap.query;

import java.util.List;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.QueryHandler;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IsExistLdapQueryHandler extends BasicLdapHandler implements QueryHandler {
	private static final Logger LOG = LoggerFactory.getLogger(IsExistLdapQueryHandler.class);
	
	private String dn;

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
			LOG.error("Error", e);
		}
	}

	public boolean isExist() {
		return exist;
	}


	@Override
	public SCOPE getScope() {
		return SCOPE.OBJECT;
	}
}
