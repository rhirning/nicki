
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.i18n.I18n;

import lombok.extern.slf4j.Slf4j;


/**
 * The Class DependendRule.
 */
// val					= dependend:confirmationType!!MAIL=min:1!max=14!!MOBILE=min:2!max:16!!
@Slf4j
@SuppressWarnings("serial")
public class DependendRule extends Rule {
	
	/** The attribut name. */
	private String attributName;
	
	/** The rules map. */
	private Map<String, List<String>> rulesMap = new HashMap<>();
	
	/** The messages. */
	List<String> messages;

	/**
	 * Instantiates a new dependend rule.
	 *
	 * @param parameter the parameter
	 */
	public DependendRule(String parameter) {
		setParameter(parameter);
		String[] parts = StringUtils.splitByWholeSeparator(parameter, "!!");
		if (parts != null && parts.length > 1) {
			this.attributName = parts[0].toLowerCase();
			for (int i = 1; i < parts.length; i++) {
				String attributeValue = StringUtils.substringBefore(parts[i], "=");
				String rules = StringUtils.substringAfter(parts[i], "=");
				if (StringUtils.isNotBlank(rules)) {
					List<String> ruleList = Arrays.asList(StringUtils.split(rules, "!"));
					rulesMap.put(attributeValue, ruleList);
				}
			}
		}
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
		boolean ok = true;
		
		List<String> rules = null;
		try {
			String attributeValue = readAttributeValue(values, this.attributName);
			rules = readAttributeValue(rulesMap, attributeValue);
		} catch (Exception e) {
			log.error("Error reading dependend rule for " + this.attributName, e);
		}
		if (rules == null) {
			rules = rulesMap.get("*");
		}
		if (rules != null) {
			for (String rule : rules) {
				try {
					Verify.verifyRule(rule, value, values);
				} catch (VerifyException e) {
					addMessage(e.getMessage());
					ok = false;
				}
			}
		}
		return ok;
	}
	
	/**
	 * Read attribute value.
	 *
	 * @param <T> the generic type
	 * @param values the values
	 * @param name the name
	 * @return the t
	 */
	private <T> T readAttributeValue(Map<String, T> values, String name) {
		if (name == null || values == null || values.size() == 0) {
			return null;
		} else {
			for (String key : values.keySet()) {
				if (StringUtils.equalsIgnoreCase(name, key)) {
					return values.get(key);
				}
			}
		}
		return null;
	}

	/**
	 * Adds the message.
	 *
	 * @param text the text
	 */
	private void addMessage(String text) {
		if (messages == null) {
			messages = new ArrayList<String>();
		}
		messages.add(text);
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".dependend", messages.toString());
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	public String toString() {
		return "email";
	}



}
