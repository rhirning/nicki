package org.mgnl.nicki.ldap.methods;


import java.io.Serializable;
import java.util.List;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicObject;

import freemarker.template.TemplateMethodModel;

public class ForeignKeyMethod implements Serializable,TemplateMethodModel {

	private static final long serialVersionUID = -5726598490077862331L;
	private DynamicObject object = null;
	private String foreignKey = null;
	private NickiContext context;
	private Class<? extends DynamicObject> classDefinition;
	
	public ForeignKeyMethod(NickiContext context, ContextSearchResult rs, String ldapName,
			Class<? extends DynamicObject> classDefinition) {
		this.context = context;
		this.foreignKey = (String) LdapHelper.getAttribute(rs, ldapName);
		this.classDefinition = classDefinition;
	}

	@Override
	public DynamicObject exec(@SuppressWarnings("rawtypes") List arguments) {
		if (object == null) {
			object = context.loadObject(this.classDefinition, this.foreignKey);
		}
		return object;
	}

	protected DynamicObject getObject() {
		return object;
	}

	protected void setObject(DynamicObject object) {
		this.object = object;
	}

	protected String getForeignKey() {
		return foreignKey;
	}

	protected NickiContext getContext() {
		return context;
	}

	public Class<? extends DynamicObject> getClassDefinition() {
		return classDefinition;
	}

}
