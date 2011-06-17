package org.mgnl.nicki.ldap.context;

import java.util.List;

import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

public interface QueryHandler {

	String getBaseDN();

	String getFilter();

	Object getConstraints();

	void handle(List<ContextSearchResult> results) throws DynamicObjectException;

}
