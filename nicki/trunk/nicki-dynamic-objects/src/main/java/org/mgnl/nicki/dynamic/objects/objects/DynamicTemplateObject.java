package org.mgnl.nicki.dynamic.objects.objects;

import java.util.Iterator;
import java.util.List;

import org.mgnl.nicki.ldap.methods.ChildrenMethod;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;


import freemarker.template.TemplateMethodModel;

@SuppressWarnings("serial")
public abstract class DynamicTemplateObject extends DynamicObject {

	@Override
	public void init(ContextSearchResult rs) throws DynamicObjectException {
		super.init(rs);
		
		for (Iterator<String> iterator = getModel().getChildren().keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			String filter = getModel().getChildren().get(key);
			put(DynamicAttribute.getGetter(key), new ChildrenMethod(getContext(), rs, filter));
		}
	}
	
	public void addMethod(String name, TemplateMethodModel method) {
		put(DynamicAttribute.getGetter(name), method);
	};
	
	public Object execute(String methodName, @SuppressWarnings("rawtypes") List arguments) throws DynamicObjectException {
		try {
			TemplateMethodModel method = (TemplateMethodModel) get(methodName);
			return method.exec(arguments);
		} catch (Exception e) {
			throw new DynamicObjectException(e);
		}		
	}


}
