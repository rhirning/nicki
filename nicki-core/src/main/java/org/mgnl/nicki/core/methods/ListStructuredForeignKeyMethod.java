
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


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("serial")
public class ListStructuredForeignKeyMethod extends ListForeignKeyMethod {
	public ListStructuredForeignKeyMethod(NickiContext context,
			ContextSearchResult rs, String ldapName,
			Class<? extends DynamicObject> classDefinition) {
		super(context, rs, ldapName, classDefinition);
	}

	public List<DynamicObject> exec(@SuppressWarnings("rawtypes") List arguments) {
		if (getObjects() == null) {
			setObjects(new ArrayList<DynamicObject>());
			for (Object key : this.getForeignKeys()) {
				String structuredForeignKey = (String) key;
				String path = StringUtils.substringBefore(structuredForeignKey, "#");
				String rest = StringUtils.substringAfter(structuredForeignKey, "#");
				String flag = StringUtils.substringBefore(rest, "#");
				String xml = StringUtils.substringAfter(rest, "#");

				DynamicObject object = getContext().loadObject(path);
				if (object != null) {
					object.put("struct:flag" , flag);
					object.put("struct:xml" , xml);
					object.put("struct" , new StructuredData(xml));
					getObjects().add(object);
				} else {
					log.debug("Could not build object: " + path);
				}
			}
		}
		return getObjects();
	}

}
