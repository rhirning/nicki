
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;;

public class DevSSOAdapter implements SSOAdapter {
	static final Logger LOG = LoggerFactory.getLogger(DevSSOAdapter.class);
	public String getName() {
		 try {
			LOG.debug("name=" + AppContext.getSystemContext().getPrincipal().getName());
			return AppContext.getSystemContext().getPrincipal().getName();
		} catch (Exception e) {
			return null;
		}
	}

	public char[] getPassword() {
		 try {
				return AppContext.getSystemContext().getPrincipal().getPassword().toCharArray();
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

}
