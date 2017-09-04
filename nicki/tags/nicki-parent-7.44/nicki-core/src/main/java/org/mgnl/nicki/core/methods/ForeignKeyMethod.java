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
package org.mgnl.nicki.core.methods;


import java.io.Serializable;
import java.util.List;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;

import freemarker.template.TemplateMethodModel;

public class ForeignKeyMethod implements Serializable,TemplateMethodModel {

	private static final long serialVersionUID = -5726598490077862331L;
	private DynamicObject object;
	private String foreignKey;
	private NickiContext context;
	private Class<? extends DynamicObject> classDefinition;
	
	public ForeignKeyMethod(NickiContext context, ContextSearchResult rs, String ldapName,
			Class<? extends DynamicObject> classDefinition) {
		this.context = context;
		this.foreignKey = (String) rs.getValue(String.class, ldapName);
		this.classDefinition = classDefinition;
	}

	public DynamicObject exec(@SuppressWarnings("rawtypes") List arguments) {
		if (object == null) {
			object = context.loadObject(this.classDefinition, this.foreignKey);
		}
		return object;
	}

	protected DynamicObject getObject() {
		return object;
	}

	protected void setObject(DynamicObject object) {
		this.object = object;
	}

	protected String getForeignKey() {
		return foreignKey;
	}

	protected NickiContext getContext() {
		return context;
	}

	public Class<? extends DynamicObject> getClassDefinition() {
		return classDefinition;
	}

}
