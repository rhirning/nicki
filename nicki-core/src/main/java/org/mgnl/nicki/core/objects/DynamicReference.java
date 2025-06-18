
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
import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.methods.ForeignKeyMethod;
import org.mgnl.nicki.core.methods.ReferenceMethod;
import org.mgnl.nicki.core.context.NickiContext;


/**
 * The Class DynamicReference.
 */
@SuppressWarnings("serial")
public class DynamicReference extends DynamicAttribute implements Serializable {

	/** The base dn. */
	private String baseDn;
	
	/** The class definition. */
	private Class<? extends DynamicObject> classDefinition;
	
	/**
	 * Instantiates a new dynamic reference.
	 *
	 * @param classDefinition the class definition
	 * @param name the name
	 * @param baseDn the base dn
	 * @param externalName the external name
	 * @param attributeClass the attribute class
	 */
	public DynamicReference(Class<? extends DynamicObject> classDefinition, String name, String baseDn, String externalName, Class<?> attributeClass) {
		super(name, externalName, attributeClass);
		this.classDefinition = classDefinition;
		setVirtual();
		this.baseDn = baseDn;
	}
	
	/**
	 * Sets the base dn.
	 *
	 * @param baseDn the new base dn
	 */
	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}
	
	/**
	 * Gets the base dn.
	 *
	 * @return the base dn
	 */
	public String getBaseDn() {
		return baseDn;
	}
	
	/**
	 * Inits the.
	 *
	 * @param context the context
	 * @param dynamicObject the dynamic object
	 * @param rs the rs
	 */
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

	/**
	 * Gets the class definition.
	 *
	 * @return the class definition
	 */
	public Class<? extends DynamicObject> getClassDefinition() {
		return classDefinition;
	}
}

