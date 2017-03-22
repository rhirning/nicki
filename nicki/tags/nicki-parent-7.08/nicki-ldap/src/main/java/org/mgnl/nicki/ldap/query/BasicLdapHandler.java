/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.query;

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




}
