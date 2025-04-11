
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
import java.util.ArrayList;
import java.util.List;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.DynamicObject;

import freemarker.template.TemplateMethodModelEx;

// TODO: Auto-generated Javadoc
/**
 * The Class DynamicLoadObjectsMethod.
 */
@SuppressWarnings("serial")
public class DynamicLoadObjectsMethod implements TemplateMethodModelEx, Serializable {

	/** The context. */
	private NickiContext context;
	
	/**
	 * Instantiates a new dynamic load objects method.
	 *
	 * @param context the context
	 */
	public DynamicLoadObjectsMethod(NickiContext context) {
		super();
		this.context = context;
	}

	/**
	 * Exec.
	 *
	 * @param arguments the arguments
	 * @return the list<? extends dynamic object>
	 */
	public List<? extends DynamicObject> exec(@SuppressWarnings("rawtypes") List arguments) {
		if (arguments != null && arguments.size() == 2) {
			String dynamicBaseDn = (String) arguments.get(0);
			String dynamicFilter = (String) arguments.get(1);
			return context.loadObjects(dynamicBaseDn, dynamicFilter);
		} else {
			return new ArrayList<DynamicObject>();
		}
	}

}
