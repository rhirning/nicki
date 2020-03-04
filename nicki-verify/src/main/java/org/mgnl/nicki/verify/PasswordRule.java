
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


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.i18n.I18n;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("serial")
public class PasswordRule extends Rule {
	int complexity = 0;
	int min = 6;
	List<String> messages;

	public PasswordRule(String parameter) {
		setParameter(parameter);
		complexity = parseInt("complexity", 0);
		min = parseInt("minlength", 6);
	}
	private int parseInt(String key, int defaultValue) {
		if (getMap().containsKey(key)) {
			try {
				return Integer.parseInt(getMap().get(key));
			} catch (NumberFormatException e) {
				log.error("PasswordRule parse error: key=" + key);
			}
		}
		return defaultValue;
	}
	@Override
	public boolean evaluate(String value, Map<String, String> values) {
		boolean ok = true;
		if (complexity > 0 && complexity > getComplexity(value)) {
			addMessage(I18n.getText(getI18nBase() + ".password.complexity",
					Integer.toString(getComplexity(value)), 
					Integer.toString(complexity)));
			ok = false;
		}
		if (min > 0 && min > getMin(value)) {
			addMessage(I18n.getText(getI18nBase() + ".password.minlength",
					Integer.toString(min)));
			ok = false;
		}
		return ok;
	}
	
	private void addMessage(String text) {
		if (messages == null) {
			messages = new ArrayList<String>();
		}
		messages.add(text);
	}
	
	private int getMin(String value) {
		return StringUtils.length(StringUtils.trim(value));
	}
	private int getComplexity(String value) {
		int complexity = 0;
		for (SET set : SET.values()) {
			if (StringUtils.containsAny(value, set.getCharacters())) {
				complexity++;
			}
		}
		return complexity;
	}

	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".password", messages.toString());
	}
	public String toString() {
		return "passwordRule";
	}
	enum SET {
		LOWER_CASE("abcdefghijklmnopqrstuvwxyz\u00c4\u00d6\u00dc"),
		UPPER_CASE("ABCDEFGHIJKLMNOPQRSTUVWXYZ\u00e4\u00f6\u00fc"),
		DIGITS("0123456789"),
		SPECIAL(
			    // ASCII symbols
			    "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}`" +
			    // Latin-1 symbols
			    "\u00a1\u00a2\u00a3\u00a4\u00a5\u00a6\u00a7\u00a8\u00a9\u00aa\u00ab\u00ac\u00ad\u00ae\u00af" +
			    "\u00b0\u00b1\u00b2\u00b3\u00b4\u00b5\u00b6\u00b7\u00b8\u00b9\u00ba\u00bb\u00bc\u00bd\u00be\u00bf" +
			    // Latin-1 math
			    "\u00d7\u00f7" +
			    // Unicode symbols
			    "\u2013\u2014\u2015\u2017\u2018\u2019\u201a\u201b\u201c\u201d\u201e\u2020\u2021\u2022\u2026\u2030\u2032\u2033" +
			    "\u2039\u203a\u203c\u203e\u2044\u204a" +
			    // Unicode currency
			    "\u20a0\u20a1\u20a2\u20a3\u20a4\u20a5\u20a6\u20a7\u20a8\u20a9\u20aa\u20ab\u20ac\u20ad\u20ae\u20af" +
			    "\u20b0\u20b1\u20b2\u20b3\u20b4\u20b5\u20b6\u20b7\u20b8\u20b9\u20ba\u20bb\u20bc\u20bd\u20be");


		private String characters;
		
		SET(String characters) {
			this.characters = characters;
		}

		public String getCharacters() {
			return characters;
		}
	}



}
