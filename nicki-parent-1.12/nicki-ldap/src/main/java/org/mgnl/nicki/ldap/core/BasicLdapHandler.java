package org.mgnl.nicki.ldap.core;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.ldap.helper.LdapHelper;
import org.mgnl.nicki.ldap.helper.LdapHelper.LOGIC;
import org.mgnl.nicki.ldap.objects.DynamicObject;

public class BasicLdapHandler {
	private NickiContext context;
	private Class<? extends DynamicObject> classDefinition = null;
	private String filter;


	public BasicLdapHandler(NickiContext context) {
		super();
		this.context = context;
	}

	public NickiContext getContext() {
		return context;
	}

	public <T extends DynamicObject> void setClassDefinition(Class<T> classDefinition) {
		this.classDefinition = classDefinition;
	}

	public Class<? extends DynamicObject> getClassDefinition() {
		return classDefinition;
	}
	
	public String getFilter() {
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotEmpty(filter)) {
			LdapHelper.addQuery(sb, filter, LOGIC.AND);
		}

		if (getClassDefinition() == null) {
			if (sb.length() == 0) {
				LdapHelper.addQuery(sb, "objectClass=*", LOGIC.AND);
			}
		} else {
			try {
				LdapHelper.addQuery(sb, getContext().getObjectClassFilter(getClassDefinition()), LOGIC.AND);
			} catch (InstantiateDynamicObjectException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}






}
