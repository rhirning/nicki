
package org.mgnl.nicki.vaadin.base.auth;

/*-
 * #%L
 * nicki-vaadin-base
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
import javax.security.auth.spi.LoginModule;

import org.mgnl.nicki.core.auth.DynamicObjectPrincipal;
import org.mgnl.nicki.core.auth.NickiLoginModule;
import org.mgnl.nicki.core.context.AppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevLoginModule extends NickiLoginModule implements LoginModule {
	private static final Logger LOG = LoggerFactory.getLogger(DevLoginModule.class);

	private String targetName;
	@Override
	public boolean login() throws LoginException {

		try {
			if (this.targetName != null) {
				setContext(AppContext.getSystemContext(this.targetName));
			}
			// TODO: separate context / loginContext
			DynamicObjectPrincipal dynamicObjectPrincipal = new DynamicObjectPrincipal(getContext().getPrincipal(), getContext(), getContext());
			setPrincipal(dynamicObjectPrincipal);
			setSucceeded(true);
			return true;
		} catch (Exception e) {
			LOG.error("Error", e);
			return false;
		}
	}
	public String getTargetName() {
		return targetName;
	}
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}
}
