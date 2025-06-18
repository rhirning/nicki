
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

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.i18n.I18n;


/**
 * The Class MaxLengthRule.
 */
public class MaxLengthRule extends Rule {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The length. */
	private int length = 0;

	/**
	 * Instantiates a new max length rule.
	 *
	 * @param value the value
	 */
	public MaxLengthRule(String value) {
		setParameter(value);
		this.length = Integer.parseInt(value);
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
		return StringUtils.length(value) <= this.length;
	}
	
	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".maxlength", getParameter());
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	public String toString() {
		return "maxLength:" + length;
	}



}
