/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.verify;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.i18n.I18n;

public class MaxLengthRule extends Rule {
	private static final long serialVersionUID = 1L;
	private int length = 0;

	public MaxLengthRule(String value) {
		setParameter(value);
		this.length = Integer.parseInt(value);
	}

	@Override
	public boolean evaluate(String value, Map<String, String> values) {
		return StringUtils.length(value) <= this.length;
	}
	
	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".maxlength", getParameter());
	}
	public String toString() {
		return "maxLength:" + length;
	}



}
