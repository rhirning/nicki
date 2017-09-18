
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


import java.util.List;
import java.util.Map;

import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.i18n.I18n;

// val					= values:value1,value2,value3

@SuppressWarnings("serial")
public class ValuesRule extends Rule {
	static final public String SEPARATOR = ",";
	private List<String> allowedValues;

	public ValuesRule(String parameter) {
		setParameter(parameter);
		allowedValues = DataHelper.getList(parameter, SEPARATOR);
	}
	
	@Override
	public boolean evaluate(String value, Map<String, String> values) {
		if (allowedValues.contains(value)) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".values", getParameter());
	}
	public String toString() {
		return "values:" + getParameter();
	}



}
