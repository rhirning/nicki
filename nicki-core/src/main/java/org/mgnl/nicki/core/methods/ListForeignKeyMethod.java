
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
import java.util.ArrayList;
import java.util.List;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.TemplateMethodModelEx;

public class ListForeignKeyMethod implements TemplateMethodModelEx, Serializable {

	private static final long serialVersionUID = -81535049844368520L;
	private static final Logger LOG = LoggerFactory.getLogger(ListForeignKeyMethod.class);
	private List<DynamicObject> objects;
	private List<Object> foreignKeys = new ArrayList<Object>();
	private NickiContext context;
	private Class<? extends DynamicObject> classDefinition;

	
	public ListForeignKeyMethod(NickiContext context, ContextSearchResult rs, String ldapName,
			Class<? extends DynamicObject> classDefinition) {
		this.context = context;
		this.foreignKeys = rs.getValues(ldapName);
		this.classDefinition = classDefinition;
	}

	public List<DynamicObject> exec(@SuppressWarnings("rawtypes") List arguments) {
		if (objects == null) {
			objects = new ArrayList<DynamicObject>();
			for (Object key : this.foreignKeys) {
				String path = (String) key;
				DynamicObject object = context.loadObject(classDefinition, path);
				if (object != null) {
					objects.add(context.loadObject(classDefinition, path));
				} else {
					LOG.debug("Could not build object: " + path);
				}
			}
		}
		return objects;
	}

	protected List<DynamicObject> getObjects() {
		return objects;
	}

	protected void setObjects(List<DynamicObject> objects) {
		this.objects = objects;
	}

	protected List<Object> getForeignKeys() {
		return foreignKeys;
	}

	protected NickiContext getContext() {
		return context;
	}

}
