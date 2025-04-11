
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

// TODO: Auto-generated Javadoc
/**
 * The Interface DynamicObjectAdapter.
 */
public interface DynamicObjectAdapter extends Serializable {
	
	/**
	 * Inits the new.
	 *
	 * @param dynamicObject the dynamic object
	 * @param parentPath the parent path
	 * @param namingValue the naming value
	 */
	void initNew(DynamicObject dynamicObject, String parentPath, String namingValue);
	
	/**
	 * Gets the parent path.
	 *
	 * @param dynamicObject the dynamic object
	 * @return the parent path
	 */
	String getParentPath(DynamicObject dynamicObject);
	
	/**
	 * Inits the existing.
	 *
	 * @param dynamicObject the dynamic object
	 * @param context the context
	 * @param path the path
	 */
	void initExisting(DynamicObject dynamicObject, NickiContext context, String path);
	
	/**
	 * Accept.
	 *
	 * @param dynamicObject the dynamic object
	 * @param rs the rs
	 * @return true, if successful
	 */
	boolean accept(DynamicObject dynamicObject, ContextSearchResult rs);
	
	/**
	 * Check attribute.
	 *
	 * @param rs the rs
	 * @param attribute the attribute
	 * @param value the value
	 * @return true, if successful
	 */
	boolean checkAttribute(ContextSearchResult rs, String attribute, String value);
	
	/**
	 * Merge.
	 *
	 * @param dynamicObject the dynamic object
	 * @param changeAttributes the change attributes
	 */
	void merge(DynamicObject dynamicObject, Map<DynamicAttribute, Object> changeAttributes);
	
	/**
	 * Gets the localized value.
	 *
	 * @param dynamicObject the dynamic object
	 * @param attributeName the attribute name
	 * @param locale the locale
	 * @return the localized value
	 */
	String getLocalizedValue(DynamicObject dynamicObject, String attributeName, String locale);
	
	/**
	 * Gets the path.
	 *
	 * @param dynamicObject the dynamic object
	 * @param parentPath the parent path
	 * @param name the name
	 * @return the path
	 */
	String getPath(DynamicObject dynamicObject, String parentPath, String name);
	
	/**
	 * Gets the object class filter.
	 *
	 * @param dynamicObject the dynamic object
	 * @return the object class filter
	 */
	String getObjectClassFilter(DynamicObject dynamicObject);
}
