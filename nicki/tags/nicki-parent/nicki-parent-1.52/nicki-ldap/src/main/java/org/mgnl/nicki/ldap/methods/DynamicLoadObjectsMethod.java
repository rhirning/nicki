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
import java.util.ArrayList;
import java.util.List;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.objects.DynamicObject;

import freemarker.template.TemplateMethodModel;

@SuppressWarnings("serial")
public class DynamicLoadObjectsMethod implements TemplateMethodModel, Serializable {

	private NickiContext context;
	
	public DynamicLoadObjectsMethod(NickiContext context) {
		super();
		this.context = context;
	}

	public List<DynamicObject> exec(@SuppressWarnings("rawtypes") List arguments) {
		if (arguments != null && arguments.size() == 2) {
			String dynamicBaseDn = (String) arguments.get(0);
			String dynamicFilter = (String) arguments.get(1);
			return context.loadObjects(dynamicBaseDn, dynamicFilter);
		} else {
			return new ArrayList<DynamicObject>();
		}
	}

}
