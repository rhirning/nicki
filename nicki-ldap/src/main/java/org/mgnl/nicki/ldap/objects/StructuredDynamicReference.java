
package org.mgnl.nicki.ldap.objects;

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

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicAttribute;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.ldap.context.LdapContext;
import org.mgnl.nicki.ldap.methods.StructuredReferenceMethod;

@SuppressWarnings("serial")
public class StructuredDynamicReference extends DynamicAttribute implements Serializable {

	private String attributeName;
	private String baseDn;
	private Class<? extends DynamicObject> classDefinition;
	public StructuredDynamicReference(Class<? extends DynamicObject> classDefinition, String name, String baseDn, String attributeName, Class<?> attributeClass) {
		super(name, name, attributeClass);
		this.classDefinition = classDefinition;
		setVirtual();
		this.baseDn = baseDn;
		this.attributeName = attributeName;
	}
	public String getAttributeName() {
		return attributeName;
	}
	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}
	public String getBaseDn() {
		return baseDn;
	}
	@Override
	public void init(NickiContext context, DynamicObject dynamicObject, ContextSearchResult rs) {
		dynamicObject.put(getGetter(getName()), new StructuredReferenceMethod((LdapContext) context, rs, this));
	}
	public Class<? extends DynamicObject> getClassDefinition() {
		return classDefinition;
	}
}

