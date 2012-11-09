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

import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.ldap.context.LdapContext;
import org.mgnl.nicki.ldap.data.QueryHandler;
import org.mgnl.nicki.ldap.objects.BaseLdapDynamicObject;

public class ObjectLoaderLdapQueryHandler extends BasicLdapHandler implements QueryHandler {
	
	private String dn = null;
	protected BaseLdapDynamicObject dynamicObject;

	public DynamicObject getDynamicObject() {
		return dynamicObject;
	}

	public ObjectLoaderLdapQueryHandler(BaseLdapDynamicObject dynamicObject) {
		super((LdapContext) dynamicObject.getContext());
		this.dynamicObject = dynamicObject;
		this.dn = dynamicObject.getPath();
	}


	public ObjectLoaderLdapQueryHandler(LdapContext context, String dn) {
		super(context);
		this.dynamicObject = null;
		this.dn = dn;
	}


	public String getBaseDN() {
		return this.dn;
	}

	public void handle(List<ContextSearchResult> results) throws DynamicObjectException {
		try {
			if (results != null && results.size() > 0) {
				if (this.dynamicObject == null) {
					try {
						if (getClassDefinition() != null) {
							dynamicObject = (BaseLdapDynamicObject) getContext().getObjectFactory().getObject(results.get(0), getClassDefinition());
						} else {
							dynamicObject = (BaseLdapDynamicObject) getContext().getObjectFactory().getObject(results.get(0));
						}
					} catch (InstantiateDynamicObjectException e) {
						throw new DynamicObjectException(e);
					}
					dynamicObject.initExisting(getContext(), dn);
				}
				this.dynamicObject.init(results.get(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public SCOPE getScope() {
		return SCOPE.OBJECT;
	}

}
