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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.ldap.context.LdapContext;
import org.mgnl.nicki.core.data.Query;

public class ObjectsLoaderLdapQueryHandler extends ObjectLoaderLdapQueryHandler {

	private List<DynamicObject> list = new ArrayList<DynamicObject>();
	
	public List<DynamicObject> getList() {
		return list;
	}

	public ObjectsLoaderLdapQueryHandler(NickiContext context, String dn, String filter) {
		super((LdapContext) context, dn);
		setFilter(filter);
	}

	public ObjectsLoaderLdapQueryHandler(NickiContext context, Query query) {
		super((LdapContext) context, query.getBaseDN());
		setFilter(query.getFilter());
	}

	@Override
	public void handle(List<ContextSearchResult> results) throws DynamicObjectException {
		for (Iterator<ContextSearchResult> iterator = results.iterator(); iterator.hasNext();) {
			if (getClassDefinition() != null) {
				try {
					ContextSearchResult rs = iterator.next();
					DynamicObject dynamicObject = getContext().getLdapObjectFactory().getObject(rs, getClassDefinition());
					list.add(dynamicObject);
				} catch (InstantiateDynamicObjectException e) {
					System.out.println(e.getMessage());
				}
			} else {
				try {
					list.add(getContext().getLdapObjectFactory().getObject(iterator.next()));
				} catch (InstantiateDynamicObjectException e) {
					throw new DynamicObjectException(e);
				}
			}
		}
	}
	


	@Override
	public SCOPE getScope() {
		return SCOPE.SUBTREE;
	}

	@Override
	public SearchControls getConstraints() {
		SearchControls constraints =super.getConstraints();
		String[] attrIDs = { "objectClass" };
		constraints.setReturningAttributes(attrIDs);

		return constraints;
	}
}
