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

import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.context.TargetFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;;

public class DevSSOAdapter implements SSOAdapter {
	static final Logger LOG = LoggerFactory.getLogger(DevSSOAdapter.class);
	private NickiAdapterLoginModule loginModule;

	public String getName() {
		Target loginTarget = getLoginTarget();
		try {
			LOG.debug(loginTarget.getName() + ": name="
					+ AppContext.getSystemContext(loginTarget.getName()).getPrincipal().getName());
			return AppContext.getSystemContext(loginTarget.getName()).getPrincipal().getName();
		} catch (Exception e) {
			return null;
		}
	}

	private Target getLoginTarget() {
		Target loginTarget;
		if (loginModule != null) {
			loginTarget = loginModule.getLoginTarget();
		} else {
			loginTarget = TargetFactory.getDefaultTarget();
		}

		return loginTarget;
	}

	public char[] getPassword() {
		Target loginTarget = getLoginTarget();
		try {
			return AppContext.getSystemContext(loginTarget.getName()).getPrincipal().getPassword().toCharArray();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public TYPE getType() {
		return TYPE.BASIC;
	}

	@Override
	public void setRequest(Object request) {
	}

	@Override
	public void init(NickiAdapterLoginModule loginModule) {
		this.loginModule = loginModule;
	}

}
