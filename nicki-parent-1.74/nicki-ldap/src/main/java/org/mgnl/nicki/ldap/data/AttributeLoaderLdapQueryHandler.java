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
package org.mgnl.nicki.ldap.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.SearchControls;

import org.mgnl.nicki.ldap.context.QueryHandler;
import org.mgnl.nicki.ldap.core.BasicLdapHandler;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

public class AttributeLoaderLdapQueryHandler extends BasicLdapHandler implements QueryHandler {

	private DynamicObject dynamicObject;
	private String[] attributes;
	private Map<String, List<Object>> lists = new HashMap<String, List<Object>>();
	public AttributeLoaderLdapQueryHandler(DynamicObject dynamicObject, String[] attributes) {
		super(dynamicObject.getContext());
		this.dynamicObject = dynamicObject;
		this.attributes = attributes;
	}

	public void handle(List<ContextSearchResult> results) throws DynamicObjectException {
		for (ContextSearchResult rs : results) {
			for(int i = 0; i < attributes.length; i++) {
				lists.put(attributes[i], LdapHelper.getAttributes(rs, attributes[i]));
			}
		}
	}

	public SearchControls getConstraints() {
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.OBJECT_SCOPE);
		// Specify the ids of the attributes to return
		constraints.setReturningAttributes(attributes);
		return constraints;
	}

	public String getBaseDN() {
		return this.dynamicObject.getPath();
	}

	public String getFilter() {
		return null;
	}

	public Map<String, List<Object>> getLists() {
		return lists;
	}
}
