/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.data;

import java.util.List;

import javax.naming.directory.SearchControls;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.context.QueryHandler;
import org.mgnl.nicki.ldap.core.BasicLdapHandler;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

public class ObjectLoaderLdapQueryHandler extends BasicLdapHandler implements QueryHandler {
	
	private String dn = null;
	protected DynamicObject dynamicObject;

	public DynamicObject getDynamicObject() {
		return dynamicObject;
	}

	public ObjectLoaderLdapQueryHandler(DynamicObject dynamicObject) {
		super(dynamicObject.getContext());
		this.dynamicObject = dynamicObject;
		this.dn = dynamicObject.getPath();
	}


	public ObjectLoaderLdapQueryHandler(NickiContext context, String dn) {
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
						dynamicObject = getContext().getObjectFactory().getObject(results.get(0));
						dynamicObject.initExisting(getContext(), dn);
					} catch (InstantiateDynamicObjectException e) {
						throw new DynamicObjectException(e);
					}
				}
				this.dynamicObject.init(results.get(0));
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

}
