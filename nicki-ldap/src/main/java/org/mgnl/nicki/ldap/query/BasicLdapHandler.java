
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


import javax.naming.directory.SearchControls;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.data.QueryHandler;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.helper.LdapHelper.LOGIC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasicLdapHandler implements QueryHandler {
	private static final Logger LOG = LoggerFactory.getLogger(BasicLdapHandler.class);
	private NickiContext context;
	private Class<? extends DynamicObject> classDefinition;
	private String filter;


	public BasicLdapHandler(NickiContext context) {
		super();
		this.context = context;
	}

	public NickiContext getContext() {
		return context;
	}

	public <T extends DynamicObject> void setClassDefinition(Class<T> classDefinition) {
		try {
			this.classDefinition = findDynamicObject(classDefinition).getClass();
		} catch (InstantiateDynamicObjectException e) {
			this.classDefinition = classDefinition;
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends DynamicObject> T findDynamicObject(Class<T> classDefinition) throws InstantiateDynamicObjectException {
		for (String dynamicObjectName : getContext().getTarget().getDynamicObjects()) {
			DynamicObject dynamicObject = getContext().getTarget().getDynamicObject(dynamicObjectName);
			if (classDefinition.isAssignableFrom(dynamicObject.getClass())){
				return (T) dynamicObject;
			}
		}
		throw new InstantiateDynamicObjectException("Could not getObject " + classDefinition);
	}

	public Class<? extends DynamicObject> getClassDefinition() {
		return classDefinition;
	}
	
	public String getFilter() {
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotBlank(filter)) {
			LdapHelper.addQuery(sb, filter, LOGIC.AND);
		}
		
		if (getClassDefinition() == null) {
			if (sb.length() == 0) {
				LdapHelper.addQuery(sb, "objectClass=*", LOGIC.AND);
			}
		} else {
			try {
				LdapHelper.addQuery(sb, getContext().getObjectClassFilter(getContext(), getClassDefinition()), LOGIC.AND);
			} catch (InstantiateDynamicObjectException e) {
				LOG.error("Error", e);
			}
		}
		return sb.toString();
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

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

	@Override
	public int getPageSize() {
		return 0;
	}

	@Override
	public boolean onePage() {
		return false;
	}




}
