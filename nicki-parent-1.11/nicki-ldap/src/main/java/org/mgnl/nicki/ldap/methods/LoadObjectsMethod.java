package org.mgnl.nicki.ldap.methods;


import java.io.Serializable;
import java.util.List;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.objects.DynamicObject;

import freemarker.template.TemplateMethodModel;

public class LoadObjectsMethod implements TemplateMethodModel, Serializable {

	private static final long serialVersionUID = -81535049844368520L;
	List<DynamicObject> objects = null;
	private String baseDn;
	private String filter;
	private DynamicObject reference = null;
	private DynamicObject dynamicObject = null;
	
	public LoadObjectsMethod(DynamicObject reference, String baseDn, String filter) {
		this.reference = reference;
		this.baseDn = baseDn;
		this.filter = filter;
	}

	public LoadObjectsMethod(NickiContext context, DynamicObject dynamicObject, String filter) {
		this.reference = dynamicObject;
		this.dynamicObject = dynamicObject;
		this.filter = filter;
	}

	public List<DynamicObject> exec(@SuppressWarnings("rawtypes") List arguments) {
		if (objects == null) {
			if (dynamicObject != null) {
				this.baseDn = dynamicObject.getPath();
			}
			objects = reference.getContext().loadObjects(baseDn, filter);
		}
		return objects;
	}

}
