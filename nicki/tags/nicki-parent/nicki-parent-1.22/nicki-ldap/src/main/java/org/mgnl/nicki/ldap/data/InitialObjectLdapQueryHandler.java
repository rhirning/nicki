package org.mgnl.nicki.ldap.data;

import java.util.List;

import javax.naming.directory.SearchControls;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.context.QueryHandler;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

public class InitialObjectLdapQueryHandler extends ObjectLoaderLdapQueryHandler implements QueryHandler {
	
	public InitialObjectLdapQueryHandler(NickiContext context, String path) {
		super(context, path);
	}

	@Override
	public void handle(List<ContextSearchResult> results) throws DynamicObjectException {
		if (results != null && results.size() > 0) {
			try {
				dynamicObject = getContext().getObjectFactory().getObject(results.get(0));
				dynamicObject.initExisting(getContext(), getBaseDN());
			} catch (InstantiateDynamicObjectException e) {
				throw new DynamicObjectException(e);
			}
		}
	}

	public SearchControls getConstraints() {
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.OBJECT_SCOPE);
		// Specify the ids of the attributes to return
		String[] attrIDs = { "objectClass" };
		constraints.setReturningAttributes(attrIDs);
		return constraints;
	}

}
