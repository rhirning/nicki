
package org.mgnl.nicki.core.data;

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


import java.util.List;

import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObjectException;

// TODO: Auto-generated Javadoc
/**
 * The Interface QueryHandler.
 */
public interface QueryHandler {
	
	/**
	 * The Enum SCOPE.
	 */
	public static enum SCOPE {
/** The object. */
OBJECT, 
 /** The onelevel. */
 ONELEVEL, 
 /** The subtree. */
 SUBTREE};

	/**
	 * Gets the base DN.
	 *
	 * @return the base DN
	 */
	String getBaseDN();

	/**
	 * Gets the filter.
	 *
	 * @return the filter
	 */
	String getFilter();

	/**
	 * Gets the constraints.
	 *
	 * @return the constraints
	 */
	Object getConstraints();

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	SCOPE getScope();

	/**
	 * Handle.
	 *
	 * @param results the results
	 * @throws DynamicObjectException the dynamic object exception
	 */
	void handle(List<ContextSearchResult> results) throws DynamicObjectException;

	/**
	 * Gets the page size.
	 *
	 * @return the page size
	 */
	int getPageSize();
	
	/**
	 * One page.
	 *
	 * @return true, if successful
	 */
	boolean onePage();
}
