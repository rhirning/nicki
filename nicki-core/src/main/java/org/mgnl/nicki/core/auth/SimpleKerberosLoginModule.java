
package org.mgnl.nicki.core.auth;

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


import javax.security.auth.login.LoginException;
import org.mgnl.nicki.core.auth.DynamicObjectPrincipal;
import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.auth.NickiLoginModule;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleKerberosLoginModule extends NickiLoginModule {
	private static final Logger LOG = LoggerFactory.getLogger(SimpleKerberosLoginModule.class);

	@Override
	public boolean login() throws LoginException {
		LOG.debug("Using " + getClass().getCanonicalName() + ", ThreadId=" + Thread.currentThread().getId());
		DynamicObject user = AppContext.getUser();
		if (user == null) {
			return false;
		}
		try {
			setContext(user.getContext().getTarget().getSystemContext(user));
		} catch (InvalidPrincipalException e) {
			LOG.debug("Error validation context for user " + user.getDisplayName(), e);
			return false;
		}

		// TODO: separate context / loginContext
		DynamicObjectPrincipal dynamicObjectPrincipal = new DynamicObjectPrincipal(user.getName(),
				getContext(), getContext());
		setPrincipal(dynamicObjectPrincipal);
		setSucceeded(true);
		
		LOG.debug("user ok " + user.getName());
		return true;
	}

}
