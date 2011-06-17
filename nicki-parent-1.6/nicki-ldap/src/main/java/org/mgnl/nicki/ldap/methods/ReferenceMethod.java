package org.mgnl.nicki.ldap.methods;


import java.io.Serializable;
import java.util.List;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicReference;

import freemarker.template.TemplateMethodModel;

public class ReferenceMethod implements TemplateMethodModel, Serializable {

	private static final long serialVersionUID = -81535049844368520L;
	List<DynamicObject> objects = null;
	DynamicReference reference;
	String path;
	NickiContext context;
	
	public ReferenceMethod(NickiContext context, ContextSearchResult rs, DynamicReference reference) {
		this.context = context;
		this.path = rs.getNameInNamespace();
		this.reference = reference;
	}

	@Override
	public List<DynamicObject> exec(@SuppressWarnings("rawtypes") List arguments) {
		if (objects == null) {
			objects = context.loadReferenceObjects(this.path, this.reference);
		}
		return objects;
	}

}
