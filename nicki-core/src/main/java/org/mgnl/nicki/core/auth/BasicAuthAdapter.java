
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


import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.context.AppContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BasicAuthAdapter implements SSOAdapter {
	private Object request;
	public String getName() {
		return getAuthPart(0);
	}

	public char[] getPassword() {
		String password = getAuthPart(1);
		if (password != null) {
			return password.toCharArray();
		}
		return new char[]{};
	}

	@Override
	public TYPE getType() {
		return TYPE.BASIC;
	}

	@Override
	public void setRequest(Object request) {
		this.request = request;
	}
	
	public Object getRequest() {
		if (this.request != null) {
			return this.request;
		} else {
			return AppContext.getRequest();
		}
	}

	protected String[] decode(final String encodedString) {
		if (StringUtils.isNotBlank(encodedString)) {
			final byte[] decodedBytes = Base64.decodeBase64(encodedString
					.getBytes());
			final String pair = new String(decodedBytes);
			final String[] userDetails = pair.split(":", 2);
			return userDetails;
		}
		return new String[]{};
	}
	
	protected String getAuthPart(int num) {
		try {
			if (getRequest() instanceof HttpServletRequest) {
				HttpServletRequest httpServletRequest = (HttpServletRequest) getRequest();
				String header = httpServletRequest.getHeader("Authorization");
				String encodedCredentials = StringUtils.substringAfter(header, " ");
				String[] decodedCredentials = decode(encodedCredentials);
				if (decodedCredentials != null) {
					return decodedCredentials[num];
				}
			}
		} catch (Exception e) {
			log.error("Error reading Basic Authentication data", e.getMessage());
		}
		return null;
	}

	@Override
	public void init(NickiAdapterLoginModule loginModule) {
	}
	
}
