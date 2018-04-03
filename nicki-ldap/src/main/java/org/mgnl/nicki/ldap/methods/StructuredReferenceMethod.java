
package org.mgnl.nicki.ldap.methods;

/*-
 * #%L
 * nicki-ldap
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
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
 * #L%
 */



import java.io.Serializable;
import java.util.List;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.ldap.core.LdapQuery;
import org.mgnl.nicki.ldap.objects.StructuredDynamicReference;

import freemarker.template.TemplateMethodModelEx;

public class StructuredReferenceMethod implements TemplateMethodModelEx, Serializable {

	private static final long serialVersionUID = -81535049844368520L;
	List<DynamicObject> objects = null;
	StructuredDynamicReference reference;
	String path;
	NickiContext context;
	
	public StructuredReferenceMethod(NickiContext context, ContextSearchResult rs, StructuredDynamicReference structuredDynamicReference) {
		this.context = context;
		this.path = rs.getNameInNamespace();
		this.reference = structuredDynamicReference;
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
