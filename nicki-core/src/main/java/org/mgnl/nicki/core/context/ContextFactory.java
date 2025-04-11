
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


import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectAdapter;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating Context objects.
 */
public interface ContextFactory {
	
	/**
	 * Gets the guest context.
	 *
	 * @param target the target
	 * @return the guest context
	 */
	NickiContext getGuestContext(Target target);
	
	/**
	 * Gets the named user context.
	 *
	 * @param target the target
	 * @param user the user
	 * @param password the password
	 * @return the named user context
	 * @throws InvalidPrincipalException the invalid principal exception
	 */
	NickiContext getNamedUserContext(Target target, DynamicObject user, String password) throws InvalidPrincipalException;

	/**
	 * Gets the system context.
	 *
	 * @param target the target
	 * @param user the user
	 * @return the system context
	 * @throws InvalidPrincipalException the invalid principal exception
	 */
	NickiContext getSystemContext(Target target, DynamicObject user) throws InvalidPrincipalException;
	
	/**
	 * Gets the adapter.
	 *
	 * @return the adapter
	 */
	DynamicObjectAdapter getAdapter();

}
