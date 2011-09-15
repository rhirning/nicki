package org.mgnl.nicki.dynamic.objects.objects;


import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.methods.DynamicLoadObjectsMethod;
import org.mgnl.nicki.ldap.methods.LoadObjectsMethod;

@SuppressWarnings("serial")
public class Function extends DynamicTemplateObject {
	public Function(NickiContext context) {
		setContext(context);
	}

	public void initDataModel()
	{
		
		addMethod("allRoles", new LoadObjectsMethod(Role.class, this, 
				Config.getProperty("nicki.system.basedn"), "objectClass=nrfRole"));

		addMethod("allResources", new LoadObjectsMethod(Resource.class, this, 
				Config.getProperty("nicki.system.basedn"), "objectClass=nrfResource"));

		addMethod("allUsers", new LoadObjectsMethod(Person.class, this, 
				Config.getProperty("nicki.data.basedn"), "objectClass=Person"));

		addMethod("objects", new DynamicLoadObjectsMethod(getContext()));

	}
}
