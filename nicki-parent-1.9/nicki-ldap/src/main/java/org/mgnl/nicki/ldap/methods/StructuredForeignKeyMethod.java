package org.mgnl.nicki.ldap.methods;


import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicObject;

import freemarker.template.TemplateMethodModel;

public class StructuredForeignKeyMethod extends ForeignKeyMethod implements Serializable,TemplateMethodModel {

	private static final long serialVersionUID = -5726598490077862331L;
	private String path;
	private String flag;
	private String xml;
	
	
	public StructuredForeignKeyMethod(NickiContext context, ContextSearchResult rs, String ldapName) {
		super(context, rs, ldapName);
		this.path = StringUtils.substringBefore(getForeignKey(), "#");
		String rest = StringUtils.substringAfter(getForeignKey(), "#");
		this.flag = StringUtils.substringBefore(rest, "#");
		this.xml = StringUtils.substringAfter(rest, "#");
		
	}

	@Override
	public DynamicObject exec(@SuppressWarnings("rawtypes") List arguments) {
		if (getObject() == null) {
			setObject(getContext().loadObject(this.path));
			getObject().put("struct:flag" , flag);
			getObject().put("struct:xml" , xml);
			getObject().put("struct" , new StructuredData(xml));
		}
		return getObject();
	}

}
