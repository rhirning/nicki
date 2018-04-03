
package org.mgnl.nicki.jcr.methods;

/*-
 * #%L
 * nicki-jcr
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

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.ChildFilter;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.TemplateMethodModelEx;

public class ChildrenMethod implements Serializable, TemplateMethodModelEx {
	private static final Logger LOG = LoggerFactory.getLogger(ChildrenMethod.class);

	private static final long serialVersionUID = -81535049844368520L;
	private List<? extends DynamicObject> objects;
	private String parent;
	private ChildFilter filter;
	private NickiContext context;
	
	public ChildrenMethod(NickiContext context, Node node, ChildFilter filter) {
		this.context = context;
		try {
			this.parent = node.getPath();
		} catch (RepositoryException e) {
			LOG.error("Error", e);
		}
		this.filter = filter;
	}

	public List<? extends DynamicObject> exec(@SuppressWarnings("rawtypes") List arguments) {
		if (objects == null) {
			
			objects = context.loadChildObjects(parent, filter);
		}
		return objects;
	}

}
