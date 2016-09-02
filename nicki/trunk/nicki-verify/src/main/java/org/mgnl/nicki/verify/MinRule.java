package org.mgnl.nicki.verify;

import java.util.Map;

import org.mgnl.nicki.core.i18n.I18n;

public class MinRule extends Rule {
	private static final long serialVersionUID = 1L;
	private Long minValue;

	public MinRule(String parameter) {
		setParameter(parameter);
		minValue = getLong(parameter);
	}

	@Override
	public boolean evaluate(String value, Map<String, String> values) {
		if (minValue != null) {
			return minValue <= getLong(value);
		}
		return true;
	}
	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".min", getParameter());
	}
	public String toString() {
		return "min:" + minValue;
	}


}
