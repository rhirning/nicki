
package org.mgnl.nicki.jcr.data;

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


import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

import org.mgnl.nicki.core.objects.ContextSearchResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JcrSearchResult implements ContextSearchResult {
	private Node node;

	public JcrSearchResult(Node node) {
		this.node = node;
	}

	public String getNameInNamespace() {
		try {
			return node.getPath();
		} catch (RepositoryException e) {
			log.error("Error", e);
		}
		return null;
	}

	@Override
	public Object getValue(Class<?> clazz, String name) {
		try {
			return node.getProperty(name);
		} catch (PathNotFoundException e) {
			log.error("Error", e);
		} catch (RepositoryException e) {
			log.error("Error", e);
		}
		return name;
	}

	@Override
	public List<Object> getValues(String name) {
		List<Object> list = new ArrayList<Object>();
		try {
			for (PropertyIterator it = this.node.getProperties(name); it.hasNext();) {
				list.add(it.next());
			}
		} catch (RepositoryException e) {
			log.error("Error", e);
		}
		return list;
	}

	@Override
	public boolean hasAttribute(String name) {
		try {
			return node.hasProperty(name);
		} catch (RepositoryException e) {
			return false;
		}
	}

}
