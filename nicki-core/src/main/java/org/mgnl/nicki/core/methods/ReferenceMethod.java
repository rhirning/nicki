
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
import org.mgnl.nicki.core.objects.DynamicReference;

import freemarker.template.TemplateMethodModelEx;


/**
 * The Class ReferenceMethod.
 */
public class ReferenceMethod implements TemplateMethodModelEx, Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -81535049844368520L;
	
	/** The objects. */
	private List<DynamicObject> objects;
	
	/** The reference. */
	private DynamicReference reference;
	
	/** The path. */
	private String path;
	
	/** The context. */
	private NickiContext context;
	
	/**
	 * Instantiates a new reference method.
	 *
	 * @param context the context
	 * @param rs the rs
	 * @param reference the reference
	 */
	public ReferenceMethod(NickiContext context, ContextSearchResult rs, DynamicReference reference) {
		this.context = context;
		this.path = rs.getNameInNamespace();
		this.reference = reference;
	}

	/**
	 * Exec.
	 *
	 * @param arguments the arguments
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public List<DynamicObject> exec(@SuppressWarnings("rawtypes") List arguments) {
		if (objects == null) {
			objects = (List<DynamicObject>) context.loadReferenceObjects(this.reference.getClassDefinition(), this);
		}
		return objects;
	}

	/**
	 * Gets the reference.
	 *
	 * @return the reference
	 */
	public DynamicReference getReference() {
		return reference;
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

}
