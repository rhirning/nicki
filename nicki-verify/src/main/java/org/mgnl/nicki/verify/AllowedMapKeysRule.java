
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


import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.i18n.I18n;

// TODO: Auto-generated Javadoc
// var					= allowedKeys:value1,value2,value3

/**
 * The Class AllowedMapKeysRule.
 */
@SuppressWarnings("serial")
public class AllowedMapKeysRule extends Rule {
	
	/** The Constant SEPARATOR. */
	static final public String SEPARATOR = ",";
	
	/** The allowed keys. */
	private List<String> allowedKeys;
	
	/** The messages. */
	private List<String> messages;

	/**
	 * Instantiates a new allowed map keys rule.
	 *
	 * @param parameter the parameter
	 */
	public AllowedMapKeysRule(String parameter) {
		setParameter(parameter);
		allowedKeys = DataHelper.getList(parameter, SEPARATOR);
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
		JsonReader reader = Json.createReader(new StringReader(value));
		JsonObject data = reader.readObject();
		for (String key : data.keySet()) {
			if (!allowedKeys.contains(key)) {
				addMessage(key);
				ok = false;
			}
		}
		return ok;
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
		return I18n.getText(getI18nBase() + ".allowedMapKeys", messages.toString());
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	public String toString() {
		return "allowedMapKeys:" + getParameter();
	}



}
