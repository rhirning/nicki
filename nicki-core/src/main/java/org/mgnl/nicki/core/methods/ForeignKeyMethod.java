
package org.mgnl.nicki.core.methods;

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
import java.util.List;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;

import freemarker.template.TemplateMethodModelEx;


/**
 * The Class ForeignKeyMethod.
 */
public class ForeignKeyMethod implements Serializable,TemplateMethodModelEx {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5726598490077862331L;
	
	/** The object. */
	private DynamicObject object;
	
	/** The foreign key. */
	private String foreignKey;
	
	/** The context. */
	private NickiContext context;
	
	/** The class definition. */
	private Class<? extends DynamicObject> classDefinition;
	
	/**
	 * Instantiates a new foreign key method.
	 *
	 * @param context the context
	 * @param rs the rs
	 * @param ldapName the ldap name
	 * @param classDefinition the class definition
	 */
	public ForeignKeyMethod(NickiContext context, ContextSearchResult rs, String ldapName,
			Class<? extends DynamicObject> classDefinition) {
		this.context = context;
		this.foreignKey = (String) rs.getValue(String.class, ldapName);
		this.classDefinition = classDefinition;
	}

	/**
	 * Exec.
	 *
	 * @param arguments the arguments
	 * @return the dynamic object
	 */
	public DynamicObject exec(@SuppressWarnings("rawtypes") List arguments) {
		if (object == null) {
			object = context.loadObject(this.classDefinition, this.foreignKey);
		}
		return object;
	}

	/**
	 * Gets the object.
	 *
	 * @return the object
	 */
	protected DynamicObject getObject() {
		return object;
	}

	/**
	 * Sets the object.
	 *
	 * @param object the new object
	 */
	protected void setObject(DynamicObject object) {
		this.object = object;
	}

	/**
	 * Gets the foreign key.
	 *
	 * @return the foreign key
	 */
	protected String getForeignKey() {
		return foreignKey;
	}

	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	protected NickiContext getContext() {
		return context;
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
