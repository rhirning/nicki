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
public class AllowedMapKeysRule extends Rule {
	static final public String SEPARATOR = ",";
	private List<String> allowedKeys;
	private List<String> messages;

	public AllowedMapKeysRule(String parameter) {
		setParameter(parameter);
		allowedKeys = DataHelper.getList(parameter, SEPARATOR);
	}
	
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
	
	private void addMessage(String text) {
		if (messages == null) {
			messages = new ArrayList<String>();
		}
		messages.add(text);
	}
	
	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".allowedMapKeys", messages.toString());
	}
	public String toString() {
		return "allowedMapKeys:" + getParameter();
	}



}
