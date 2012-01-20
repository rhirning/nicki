/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
