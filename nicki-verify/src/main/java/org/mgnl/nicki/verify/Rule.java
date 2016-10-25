package org.mgnl.nicki.verify;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("serial")
public abstract class Rule implements Serializable {
	public static final String MAP_SEPARATOR = "!";
	public static final String MAP_EQUAL = "=";
	private String parameter;
	private Map<String, String> map = new HashMap<String, String>();
	
	public Rule() {
		super();
	}

	public abstract boolean evaluate(String value, Map<String, String> values);
	
	public static long getLong(String value) {
		value = StringUtils.strip(value);
		if (StringUtils.contains(value, ",")) {
			value = StringUtils.substringBefore(value, ",");
		}
		if (StringUtils.contains(value, ".")) {
			value = StringUtils.remove(value, ".");
		}
		return Long.valueOf(value);
	}
	
	public static double getNumber(String value) throws NumberFormatException {
		value = StringUtils.strip(value);
		if (StringUtils.contains(value, ".")) {
			value = StringUtils.remove(value, ".");
		}
		if (StringUtils.contains(value, ",")) {
			value = StringUtils.replace(value, ",", ".");
		}
		return Double.valueOf(value);
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
		initMap();
	}

	private void initMap() {
		map.clear();
		if (this.parameter != null) {
			String [] entries = StringUtils.split(parameter, MAP_SEPARATOR);
			for (String entry : entries) {
				if (StringUtils.contains(entry, MAP_EQUAL)) {
					map.put(StringUtils.substringBefore(entry, MAP_EQUAL), 
							StringUtils.substringAfter(entry, MAP_EQUAL));
				}
			}
		}
	}

	public abstract String getMessage();
	

	public String getI18nBase() {
		return "nicki.verify.message";
	}

	public Map<String, String> getMap() {
		return map;
	}

}
