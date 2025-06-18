
package org.mgnl.nicki.ldap.query;

/*-
 * #%L
 * nicki-ldap
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


import org.mgnl.nicki.core.context.NickiContext;


/**
 * The Class SubObjectsLoaderQueryHandler.
 */
public class SubObjectsLoaderQueryHandler extends ObjectsLoaderQueryHandler {

	/**
	 * Instantiates a new sub objects loader query handler.
	 *
	 * @param context the context
	 * @param parent the parent
	 * @param filter the filter
	 */
	public SubObjectsLoaderQueryHandler(NickiContext context, String parent, String filter) {
		super(context, parent, filter);
	}
	
	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	@Override
	public SCOPE getScope() {
		return SCOPE.ONELEVEL;
	}

}
