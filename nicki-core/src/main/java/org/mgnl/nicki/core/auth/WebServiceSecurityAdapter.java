
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


import java.util.List;
import java.util.Map;

import jakarta.xml.ws.handler.MessageContext;

import org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;


/**
 * SSOAdapter to retrieve credentials from WebService MessageContext.
 */
@Slf4j
public class WebServiceSecurityAdapter extends BasicAuthAdapter implements SSOAdapter {

	
	/**
	 * Gets the auth part.
	 *
	 * @param num the num
	 * @return the auth part
	 */
	@Override
	protected String getAuthPart(int num) {
		try {
			if (getRequest() instanceof MessageContext  ) {
				MessageContext   messageContext = (MessageContext ) getRequest();
				
				Map<?,?> http_headers = (Map<?,?>) messageContext.get(MessageContext.HTTP_REQUEST_HEADERS);
		        List<?> authList = (List<?>) http_headers.get("Authorization");
		        
				String header = authList.get(0).toString();
				String encodedCredentials = StringUtils.substringAfter(header, " ");
				return decode(encodedCredentials)[num];
			}
		} catch (Exception e) {
			log.error("Error reading Basic Authentication data", e);
		}
		return null;
	}
}
