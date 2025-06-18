
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

/**
 * The Class ValuesRule.
 */
@SuppressWarnings("serial")
public class ValuesRule extends Rule {
	
	/** The Constant SEPARATOR. */
	static final public String SEPARATOR = ",";
	
	/** The allowed values. */
	private List<String> allowedValues;

	/**
	 * Instantiates a new values rule.
	 *
	 * @param parameter the parameter
	 */
	public ValuesRule(String parameter) {
		setParameter(parameter);
		allowedValues = DataHelper.getList(parameter, SEPARATOR);
	}
	
	/**
	 * Evaluate.
	 *
	 * @param value the value
	 * @param values the values
	 * @return true, if successful
	 */
	@Override
	public boolean evaluate(String value, Map<String, String> values) {
		if (allowedValues.contains(value)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".values", getParameter());
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	public String toString() {
		return "values:" + getParameter();
	}



}
