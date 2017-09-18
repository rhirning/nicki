
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


import java.util.Calendar;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.i18n.I18n;

public class DateRule extends Rule {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean evaluate(String value, Map<String, String> values) {
		try {
			DataHelper.dateFromDisplayDay(value);
			String[] parts = StringUtils.split(value, ".");
			Integer day = Integer.parseInt(parts[0]);
			if (day < 1 || day > 31) {
				return false;
			}
			Integer month = Integer.parseInt(parts[1]);
			if (month < 1 || month > 12) {
				return false;
			}
			Integer year = Integer.parseInt(parts[2]);
			int now = Calendar.getInstance().get(Calendar.YEAR);
			if (year < now - 200 || year > now + 200) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".date");
	}

	public String toString() {
		return "date";
	}
}
