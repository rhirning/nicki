package org.mgnl.nicki.verify;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.i18n.I18n;

public class MaxRule extends Rule {
	private static final long serialVersionUID = 1L;
	private Long maxValue;

	public MaxRule(String parameter) {
		setParameter(parameter);
		maxValue = getLong(parameter);
	}

	@Override
	public boolean evaluate(String value, Map<String, String> values) {
		if (maxValue != null &&  StringUtils.isNotEmpty(value)) {
			try {
				return maxValue >= getLong(value);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".max", getParameter());
	}
	public String toString() {
		return "max:" + maxValue;
	}



}
