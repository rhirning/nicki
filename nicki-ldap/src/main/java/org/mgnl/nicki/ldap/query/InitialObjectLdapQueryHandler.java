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

import java.util.List;

import javax.naming.directory.SearchControls;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.QueryHandler;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObjectException;

public class InitialObjectLdapQueryHandler extends ObjectLoaderLdapQueryHandler implements QueryHandler {
	
	public InitialObjectLdapQueryHandler(NickiContext context, String path) {
		super(context, path);
	}

	@Override
	public void handle(List<ContextSearchResult> results) throws DynamicObjectException {
		if (results != null && results.size() > 0) {
			try {
				dynamicObject = getContext().getObjectFactory().getObject(results.get(0));
				getContext().getAdapter().initExisting(dynamicObject, getContext(), getBaseDN());
			} catch (Exception e) {
				throw new DynamicObjectException(e);
			}
		}
	}

	public SearchControls getConstraints() {
		SearchControls constraints = super.getConstraints();
		String[] attrIDs = { "objectClass" };
		constraints.setReturningAttributes(attrIDs);
		return constraints;
	}

}
