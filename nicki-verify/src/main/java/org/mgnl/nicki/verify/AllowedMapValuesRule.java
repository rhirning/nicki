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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.i18n.I18n;

// var					= allowedKeys:value1,value2,value3

@SuppressWarnings("serial")
public class AllowedMapValuesRule extends Rule {
	static final public String SEPARATOR = ",";
	private List<String> allowedValues;
	private List<String> messages;

	public AllowedMapValuesRule(String parameter) {
		setParameter(parameter);
		allowedValues = DataHelper.getList(parameter, SEPARATOR);
	}
	
	@Override
	public boolean evaluate(String value, Map<String, String> values) {
		boolean ok = true;
		JsonReader reader = Json.createReader(new StringReader(value));
		JsonObject data = reader.readObject();
		for (String key : data.keySet()) {
			if (!allowedValues.contains(data.getString(key))) {
				addMessage(key);
				ok = false;
			}
		}
		return ok;
	}
	
	private void addMessage(String text) {
		if (messages == null) {
			messages = new ArrayList<String>();
		}
		messages.add(text);
	}
	
	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".allowedMapValues", messages.toString());
	}
	public String toString() {
		return "allowedMapValues:" + getParameter();
	}



}
