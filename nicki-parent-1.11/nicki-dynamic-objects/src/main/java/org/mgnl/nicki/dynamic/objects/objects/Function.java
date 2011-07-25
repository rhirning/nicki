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
		
		addMethod("allRoles", new LoadObjectsMethod(this, 
				Config.getProperty("nicki.system.basedn", "o=system"), "objectClass=nrfRole"));

		addMethod("allResources", new LoadObjectsMethod(this, 
				Config.getProperty("nicki.system.basedn", "o=system"), "objectClass=nrfResource"));

		addMethod("allShops", new LoadObjectsMethod(this, 
				Config.getProperty("nicki.basedn", "o=utopia"), "objectClass=nickiShop"));

		addMethod("objects", new DynamicLoadObjectsMethod(getContext()));

	}
}
