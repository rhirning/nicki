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
	
	public static final String TEMPLATE_NAME = "ou";
	public static final String ATTRIBUTE_DATA = "nickiTemplateData";
	public static final String ATTRIBUTE_PART = "nickiTemplatePart";
	public static final String PART_SEPARATOR = "=";
	private String dn = null;
	private DynamicObject dynamicObject = null;

	public DynamicObject getDynamicObject() {
		return dynamicObject;
	}


	public ObjectLoaderLdapQueryHandler(NickiContext context, String dn) {
		super(context);
		this.dn = dn;
	}


	@Override
	public String getBaseDN() {
		return this.dn;
	}

	@Override
	public void handle(List<ContextSearchResult> results) throws DynamicObjectException {
		try {
			if (results != null && results.size() > 0) {
				if (getClassDefinition() != null) {
					this.dynamicObject = getContext().getObjectFactory().getObject(results.get(0), getClassDefinition());
				} else {
					this.dynamicObject = getContext().getObjectFactory().getObject(results.get(0));
				}
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
