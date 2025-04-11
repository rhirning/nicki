
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


import java.util.List;

import org.mgnl.nicki.core.methods.ChildrenMethod;


import freemarker.template.TemplateMethodModelEx;

// TODO: Auto-generated Javadoc
/**
 * The Class DynamicTemplateObject.
 */
@SuppressWarnings("serial")
public abstract class DynamicTemplateObject extends BaseDynamicObject {

	/**
	 * Inits the.
	 *
	 * @param rs the rs
	 * @throws DynamicObjectException the dynamic object exception
	 */
	@Override
	public void init(ContextSearchResult rs) throws DynamicObjectException {
		super.init(rs);
		
		for (String key :  getModel().getChildren().keySet()) {
			ChildFilter filter = getModel().getChildren().get(key);
			put(DynamicAttribute.getGetter(key), new ChildrenMethod(getContext(), rs, filter));
		}
	}
	
	/**
	 * Adds the method.
	 *
	 * @param name the name
	 * @param method the method
	 */
	public void addMethod(String name, TemplateMethodModelEx method) {
		put(DynamicAttribute.getGetter(name), method);
	};
	
	/**
	 * Execute.
	 *
	 * @param methodName the method name
	 * @param arguments the arguments
	 * @return the object
	 * @throws DynamicObjectException the dynamic object exception
	 */
	public Object execute(String methodName, @SuppressWarnings("rawtypes") List arguments) throws DynamicObjectException {
		try {
			TemplateMethodModelEx method = (TemplateMethodModelEx) get(methodName);
			return method.exec(arguments);
		} catch (Exception e) {
			throw new DynamicObjectException(e);
		}		
	}


}
