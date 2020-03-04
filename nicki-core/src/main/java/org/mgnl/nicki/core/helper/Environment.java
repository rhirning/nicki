
package org.mgnl.nicki.core.helper;

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


import java.io.Serializable;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.commons.lang.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("serial")
public class Environment implements Serializable{
	
	public static String getProperty(String name) {
		if (getFromJndiContext(name) != null) {
			return getFromJndiContext(name);
		}
		if (getFromSystemProperty(name) != null) {
			return getFromSystemProperty(name);
		}
		if (getFromSystemEnvironment(name) != null) {
			return getFromSystemEnvironment(name);
		}
		
		return null;
	}

	public static String getFromJndiContext(String name) {
		// Check JNDI environment
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			String value = (String) envCtx.lookup(name);
			if (StringUtils.isNotBlank(value)) {
				return value;
			}
		} catch (Exception e) {
			log.debug("Error accessing JNDI: " + name, e);
		}
		return null;
	}

	public static String getFromSystemProperty(String name) {
		try {
			return System.getProperty(name);
		} catch (Exception e) {
			log.debug("Error accessing System.getProperty: " + name, e);
		}
		return null;
	}

	public static String getFromSystemEnvironment(String name) {
		try {
			return System.getenv(name);
		} catch (Exception e) {
			log.debug("Error accessing System.getenv: " + name, e);
		}
		return null;
	}

}
