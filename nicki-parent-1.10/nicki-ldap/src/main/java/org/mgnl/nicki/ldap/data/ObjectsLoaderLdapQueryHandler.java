package org.mgnl.nicki.ldap.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.core.LdapQuery;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

public class ObjectsLoaderLdapQueryHandler extends ObjectLoaderLdapQueryHandler {

	private String filter;
	private List<DynamicObject> list = new ArrayList<DynamicObject>();
	
	public List<DynamicObject> getList() {
		return list;
	}

	public ObjectsLoaderLdapQueryHandler(NickiContext context, String dn, String filter) {
		super(context, dn);
		this.filter = filter;
	}

	public ObjectsLoaderLdapQueryHandler(NickiContext context, LdapQuery query) {
		super(context, query.getBaseDN());
		this.filter = query.getFilter();
	}

	@Override
	public String getFilter() {
		return this.filter;
	}

	@Override
	public void handle(List<ContextSearchResult> results) throws DynamicObjectException {
		for (Iterator<ContextSearchResult> iterator = results.iterator(); iterator.hasNext();) {
			try {
				list.add(getContext().getObjectFactory().getObject(iterator.next()));
			} catch (InstantiateDynamicObjectException e) {
				throw new DynamicObjectException(e);
			}
		}
	}

	@Override
	public SearchControls getConstraints() {
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		return constraints;
	}
}
