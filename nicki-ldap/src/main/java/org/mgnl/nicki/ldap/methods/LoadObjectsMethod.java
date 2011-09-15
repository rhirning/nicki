package org.mgnl.nicki.ldap.methods;


import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.helper.LdapHelper.LOGIC;
import org.mgnl.nicki.ldap.objects.DynamicObject;

import freemarker.template.TemplateMethodModel;

public class LoadObjectsMethod implements TemplateMethodModel, Serializable {

	private static final long serialVersionUID = -81535049844368520L;
	List<? extends DynamicObject> objects = null;
	private String baseDn;
	private String filter;
	private DynamicObject reference = null;
	private Class<? extends DynamicObject> classDefinition;
	
	public LoadObjectsMethod(Class<? extends DynamicObject> classDefinition, DynamicObject reference, String baseDn, String filter) {
		this.classDefinition = classDefinition;
		this.reference = reference;
		this.baseDn = baseDn;
		this.filter = filter;
	}

	public LoadObjectsMethod(Class<? extends DynamicObject> classDefinition, DynamicObject reference, String filter) {
		this.classDefinition = classDefinition;
		this.reference = reference;
		this.filter = filter;
	}

	public List<? extends DynamicObject> exec(@SuppressWarnings("rawtypes") List arguments) {
		if (objects == null) {
			StringBuffer sb = new StringBuffer();
			if (StringUtils.isNotEmpty(filter)) {
				LdapHelper.addQuery(sb, filter, LOGIC.AND);
			}
			if (arguments != null && arguments.size() > 0) {
				LdapHelper.addQuery(sb, (String) arguments.get(0), LOGIC.AND);
			}
			if (baseDn == null) {
				this.baseDn = reference.getPath();
			}
			objects = reference.getContext().loadObjects(classDefinition, baseDn, sb.toString());
		}
		return objects;
	}

}
