
package org.mgnl.nicki.jcr.objects;

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


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DynamicObject
public class JcrPerson extends Person implements JcrDynamicObject {
	private static final Logger LOG = LoggerFactory.getLogger(JcrPerson.class);

	private static final long serialVersionUID = -7661216202997423875L;
	
	private NodeDynamicTemplateObject baseObject = new NodeDynamicTemplateObject();

	@Override
	public boolean accept(Node node) {
		try {
			return StringUtils.startsWith(node.getPath(), "/users/");
		} catch (RepositoryException e) {
			LOG.error("Error", e);
		}
		return false;
	}

	@Override
	public Node getNode() {
		return baseObject.getNode();
	}

	@Override
	public void setNode(Node node) {
		baseObject.setNode(node);
	}

	@Override
	public void init(NickiContext context, Node node) throws DynamicObjectException {
		baseObject.init(context, node);
	}


}
