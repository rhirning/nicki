package org.mgnl.nicki.verify;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

public abstract class Rule implements Serializable {
	private static final long serialVersionUID = 1L;
	private String parameter = null;
	
	public Rule() {
		super();
	}

	public abstract boolean evaluate(String value);
	
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
	}

	public abstract String getMessage();
	

	public String getI18nBase() {
		return "nicki.verify.message";
	}

}
