
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

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.i18n.I18n;

public class MinRule extends Rule {
	private static final long serialVersionUID = 1L;
	private Long minValue;

	public MinRule(String parameter) {
		setParameter(parameter);
		minValue = getLong(parameter);
	}

	@Override
	public boolean evaluate(String value, Map<String, String> values) {
		if (minValue != null &&  StringUtils.isNotBlank(value)) {
			return minValue <= getLong(value);
		}
		return true;
	}
	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".min", getParameter());
	}
	public String toString() {
		return "min:" + minValue;
	}


}
