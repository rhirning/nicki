
package org.mgnl.nicki.core.objects;

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
import java.util.Map;

import org.mgnl.nicki.core.context.NickiContext;

public interface DynamicObjectAdapter extends Serializable {
	
	void initNew(DynamicObject dynamicObject, String parentPath, String namingValue);
	String getParentPath(DynamicObject dynamicObject);
	void initExisting(DynamicObject dynamicObject, NickiContext context, String path);
	boolean accept(DynamicObject dynamicObject, ContextSearchResult rs);
	boolean checkAttribute(ContextSearchResult rs, String attribute, String value);
	void merge(DynamicObject dynamicObject, Map<DynamicAttribute, Object> changeAttributes);
	String getLocalizedValue(DynamicObject dynamicObject, String attributeName, String locale);
	String getPath(DynamicObject dynamicObject, String parentPath, String name);
	String getObjectClassFilter(DynamicObject dynamicObject);
}
