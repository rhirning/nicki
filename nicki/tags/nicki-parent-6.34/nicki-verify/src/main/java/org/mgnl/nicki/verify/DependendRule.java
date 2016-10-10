package org.mgnl.nicki.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.i18n.I18n;

// val					= dependend:confirmationType!!MAIL=min:1!max=14!!MOBILE=min:2!max:16!!

@SuppressWarnings("serial")
public class DependendRule extends Rule {
	private String attributName;
	private Map<String, List<String>> rulesMap = new HashMap<>();
	
	List<String> messages;

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
	
	@Override
	public boolean evaluate(String value, Map<String, String> values) {
		boolean ok = true;
		
		String attributeValue = values.get(attributName);
		List<String> rules = rulesMap.get(attributeValue);
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
	
	private void addMessage(String text) {
		if (messages == null) {
			messages = new ArrayList<String>();
		}
		messages.add(text);
	}

	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".dependend", messages.toString());
	}
	public String toString() {
		return "email";
	}



}
