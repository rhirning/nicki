
package org.mgnl.nicki.verify;

/*-
 * #%L
 * nicki-verify
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


import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.i18n.I18n;

public class EmailRule extends Rule {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean evaluate(String value, Map<String, String> values) {
		try {
			new InternetAddress(value);
			if (!hasNameAndDomain(value)) {
				return false;
			}
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	private static boolean hasNameAndDomain(String aEmailAddress) {
		String[] tokens = aEmailAddress.split("@");
		if (tokens.length != 2) return false;
		
		return StringUtils.isNotEmpty(tokens[0])
			&& StringUtils.split(tokens[1], ".").length > 1;
	}

	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".email");
	}
	public String toString() {
		return "email";
	}


}
