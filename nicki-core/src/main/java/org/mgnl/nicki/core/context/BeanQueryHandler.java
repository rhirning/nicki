
package org.mgnl.nicki.core.context;

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


import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;

// TODO: Auto-generated Javadoc
/**
 * The Interface BeanQueryHandler.
 */
public interface BeanQueryHandler {
	
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
	 * @param ctx the ctx
	 * @param results the results
	 * @throws NamingException the naming exception
	 */
	void handle(NickiContext ctx, NamingEnumeration<SearchResult> results) throws NamingException;
}
