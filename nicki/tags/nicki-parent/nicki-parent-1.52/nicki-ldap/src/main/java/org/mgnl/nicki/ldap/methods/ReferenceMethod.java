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
package org.mgnl.nicki.ldap.methods;


import java.io.Serializable;
import java.util.List;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.core.LdapQuery;
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

	@SuppressWarnings("unchecked")
	public List<DynamicObject> exec(@SuppressWarnings("rawtypes") List arguments) {
		if (objects == null) {
			LdapQuery query = new LdapQuery(path, reference);
			objects = (List<DynamicObject>) context.loadReferenceObjects(this.reference.getClassDefinition(), query);
		}
		return objects;
	}

}
