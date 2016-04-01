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
import java.util.List;

import javax.naming.directory.SearchControls;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.core.data.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectsLoaderQueryHandler extends ObjectLoaderLdapQueryHandler {
	private static final Logger LOG = LoggerFactory.getLogger(ObjectsLoaderQueryHandler.class);

	private List<DynamicObject> list = new ArrayList<DynamicObject>();
	
	public List<DynamicObject> getList() {
		return list;
	}

	public ObjectsLoaderQueryHandler(NickiContext context, String dn, String filter) {
		super(context, dn);
		setFilter(filter);
	}

	public ObjectsLoaderQueryHandler(NickiContext context, Query query) {
		super(context, query.getBaseDN());
		setFilter(query.getFilter());
	}

	@Override
	public void handle(List<ContextSearchResult> results) throws DynamicObjectException {
		for (ContextSearchResult rs : results) {
			DynamicObject dynamicObject = null;
			if (getClassDefinition() != null) {
				try {
					dynamicObject = getContext().getObjectFactory().getObject(rs, getClassDefinition());
				} catch (InstantiateDynamicObjectException e) {
					dynamicObject = null;
					LOG.debug(e.getMessage());
				}
			} else {
				try {
					dynamicObject = getContext().getObjectFactory().getObject(rs);
				} catch (InstantiateDynamicObjectException e) {
					dynamicObject = null;
					throw new DynamicObjectException(e);
				}
			}
			if (dynamicObject != null) {
				list.add(dynamicObject);
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
