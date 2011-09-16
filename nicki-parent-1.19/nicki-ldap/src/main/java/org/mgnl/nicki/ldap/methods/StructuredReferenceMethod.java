package org.mgnl.nicki.ldap.methods;


import java.io.Serializable;
import java.util.List;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.core.LdapQuery;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicReference;
import org.mgnl.nicki.ldap.objects.StructuredDynamicReference;

import freemarker.template.TemplateMethodModel;

public class StructuredReferenceMethod implements TemplateMethodModel, Serializable {

	private static final long serialVersionUID = -81535049844368520L;
	List<DynamicObject> objects = null;
	StructuredDynamicReference reference;
	String path;
	NickiContext context;
	
	public StructuredReferenceMethod(NickiContext context, ContextSearchResult rs, StructuredDynamicReference structuredDynamicReference) {
		this.context = context;
		this.path = rs.getNameInNamespace();
		this.reference = structuredDynamicReference;
	}

	@Override
	public List<DynamicObject> exec(@SuppressWarnings("rawtypes") List arguments) {
		if (objects == null) {
			LdapQuery query = new LdapQuery(path, reference);
			objects = (List<DynamicObject>) context.loadReferenceObjects(this.reference.getClassDefinition(), query);
		}
		return objects;
	}

}
