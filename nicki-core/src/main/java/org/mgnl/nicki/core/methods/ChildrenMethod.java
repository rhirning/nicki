
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
import org.mgnl.nicki.core.objects.ChildFilter;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;

import freemarker.template.TemplateMethodModelEx;

// TODO: Auto-generated Javadoc
/**
 * The Class ChildrenMethod.
 */
public class ChildrenMethod implements Serializable, TemplateMethodModelEx {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -81535049844368520L;
	
	/** The objects. */
	private List<? extends DynamicObject> objects;
	
	/** The parent. */
	private String parent;
	
	/** The filter. */
	private ChildFilter filter;
	
	/** The context. */
	private NickiContext context;
	
	/**
	 * Instantiates a new children method.
	 *
	 * @param context the context
	 * @param rs the rs
	 * @param filter the filter
	 */
	public ChildrenMethod(NickiContext context, ContextSearchResult rs, ChildFilter filter) {
		this.context = context;
		this.parent = rs.getNameInNamespace();
		this.filter = filter;
	}

	/**
	 * Exec.
	 *
	 * @param arguments the arguments
	 * @return the list<? extends dynamic object>
	 */
	public List<? extends DynamicObject> exec(@SuppressWarnings("rawtypes") List arguments) {
		if (objects == null) {
			objects = context.loadChildObjects(parent, filter);
		}
		return objects;
	}

}
