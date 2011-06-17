package org.mgnl.nicki.ldap.data.jndi;

import javax.naming.directory.SearchResult;

import org.mgnl.nicki.ldap.objects.ContextAttributes;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;

public class JndiSearchResult implements ContextSearchResult {
	private SearchResult rs;

	public JndiSearchResult(SearchResult rs) {
		this.rs = rs;
	}

	@Override
	public String getNameInNamespace() {
		return rs.getNameInNamespace();
	}

	@Override
	public ContextAttributes getAttributes() {		
		return new JndiAttributes(rs.getAttributes());
	}

}
