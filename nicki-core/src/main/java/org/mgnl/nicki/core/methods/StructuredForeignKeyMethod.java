
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

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;

import freemarker.template.TemplateMethodModelEx;

public class StructuredForeignKeyMethod extends ForeignKeyMethod implements Serializable,TemplateMethodModelEx {

	private static final long serialVersionUID = -5726598490077862331L;
	private String path;
	private String flag;
	private String xml;
	
	
	public StructuredForeignKeyMethod(NickiContext context, ContextSearchResult rs, String ldapName,
			Class<? extends DynamicObject> classDefinition) {
		super(context, rs, ldapName,classDefinition);
		this.path = StringUtils.substringBefore(getForeignKey(), "#");
		String rest = StringUtils.substringAfter(getForeignKey(), "#");
		this.flag = StringUtils.substringBefore(rest, "#");
		this.xml = StringUtils.substringAfter(rest, "#");
		
	}

	@Override
	public DynamicObject exec(@SuppressWarnings("rawtypes") List arguments) {
		if (getObject() == null) {
			setObject(getContext().loadObject(getClassDefinition(), this.path));
			getObject().put("struct:flag" , flag);
			getObject().put("struct:xml" , xml);
			getObject().put("struct" , new StructuredData(xml));
		}
		return getObject();
	}

}
