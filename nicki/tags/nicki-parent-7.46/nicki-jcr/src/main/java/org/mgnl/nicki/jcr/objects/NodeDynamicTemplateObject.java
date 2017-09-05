/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.jcr.objects;

import java.util.List;

import javax.jcr.Node;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.ChildFilter;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.jcr.methods.ChildrenMethod;


import freemarker.template.TemplateMethodModel;

@SuppressWarnings("serial")
public class NodeDynamicTemplateObject extends BaseJcrDynamicObject {

	@Override
	public void init(NickiContext context, Node node) throws DynamicObjectException {
		super.init(context, node);
		
		for (String key : getModel().getChildren().keySet()) {
			ChildFilter filter = getModel().getChildren().get(key);
			put(DynamicJcrAttribute.getGetter(key), new ChildrenMethod(getContext(),node, filter));
		}
	}
	
	public void addMethod(String name, TemplateMethodModel method) {
		put(DynamicJcrAttribute.getGetter(name), method);
	};
	
	public Object execute(String methodName, @SuppressWarnings("rawtypes") List arguments) throws DynamicObjectException {
		try {
			TemplateMethodModel method = (TemplateMethodModel) get(methodName);
			return method.exec(arguments);
		} catch (Exception e) {
			throw new DynamicObjectException(e);
		}		
	}


}
