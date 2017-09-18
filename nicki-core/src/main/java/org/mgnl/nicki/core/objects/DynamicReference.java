
package org.mgnl.nicki.core.objects;

/*-
 * #%L
 * nicki-core
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
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.methods.ForeignKeyMethod;
import org.mgnl.nicki.core.methods.ReferenceMethod;
import org.mgnl.nicki.core.context.NickiContext;

@SuppressWarnings("serial")
public class DynamicReference extends DynamicAttribute implements Serializable {

	private String baseDn;
	private Class<? extends DynamicObject> classDefinition;
	public DynamicReference(Class<? extends DynamicObject> classDefinition, String name, String baseDn, String externalName, Class<?> attributeClass) {
		super(name, externalName, attributeClass);
		this.classDefinition = classDefinition;
		setVirtual();
		this.baseDn = baseDn;
	}
	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}
	public String getBaseDn() {
		return baseDn;
	}
	@Override
	public void init(NickiContext context, DynamicObject dynamicObject, ContextSearchResult rs) {
		if (isMultiple()) {
			dynamicObject.put(getMultipleGetter(getName()),
					new ReferenceMethod(context, rs, this));

		} else {
			String value = (String) rs.getValue(getType(), getExternalName());
			if (StringUtils.isNotEmpty(value)) {
				dynamicObject.put(getName(), value);
				dynamicObject.put(getGetter(getName()),
						new ForeignKeyMethod(context, rs, getExternalName(), getForeignKeyClass()));
			}
		}
	}

	public Class<? extends DynamicObject> getClassDefinition() {
		return classDefinition;
	}
}

