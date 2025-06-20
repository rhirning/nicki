
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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


/**
 * The Class ChildFilter.
 */
public class ChildFilter implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3256064092279768874L;
	
	/** The filter. */
	private String filter;
	
	/** The object filters. */
	private List<Class<? extends DynamicObject>> objectFilters;
	
	/**
	 * Gets the filter.
	 *
	 * @return the filter
	 */
	public String getFilter() {
		return filter;
	}
	
	/**
	 * Sets the filter.
	 *
	 * @param filter the new filter
	 */
	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	/**
	 * Gets the object filters.
	 *
	 * @return the object filters
	 */
	public List<Class<? extends DynamicObject>> getObjectFilters() {
		return objectFilters;
	}
	
	/**
	 * Sets the object filters.
	 *
	 * @param objectFilters the new object filters
	 */
	public void setObjectFilters(List<Class<? extends DynamicObject>> objectFilters) {
		this.objectFilters = objectFilters;
	}
	
	/**
	 * Adds the object filter.
	 *
	 * @param objectFilter the object filter
	 */
	public void addObjectFilter(Class<? extends DynamicObject> objectFilter) {
		if (objectFilters == null) {
			objectFilters = new ArrayList<Class<? extends DynamicObject>>();
		}
		objectFilters.add(objectFilter);
	}
	
	/**
	 * Checks for filter.
	 *
	 * @return true, if successful
	 */
	public boolean hasFilter() {
		return StringUtils.isNotBlank(filter);
	}
	
	/**
	 * Checks for object filters.
	 *
	 * @return true, if successful
	 */
	public boolean hasObjectFilters() {
		return objectFilters != null && objectFilters.size() > 0;
	}
}
