
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mgnl.nicki.core.i18n.I18n;

public class RegExRule extends Rule {
	private static final long serialVersionUID = 1L;
	Pattern pattern;

	public RegExRule(String parameter) {
		setParameter(parameter);
		pattern = Pattern.compile(parameter);
	}

	@Override
	public boolean evaluate(String value, Map<String, String> values) {

		Matcher matcher = pattern.matcher(value);
		//return (StringUtils.equals(value, matcher.group()));
		return matcher.matches();
	}
	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".regex", getParameter());
	}
	public String toString() {
		return "regex:" + getParameter();
	}



}
