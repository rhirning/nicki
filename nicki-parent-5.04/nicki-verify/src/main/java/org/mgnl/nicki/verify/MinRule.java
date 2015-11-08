package org.mgnl.nicki.verify;

import org.mgnl.nicki.core.i18n.I18n;

public class MinRule extends Rule {
	private static final long serialVersionUID = 1L;
	Long minValue = null;

	public MinRule(String parameter) {
		setParameter(parameter);
		minValue = getLong(parameter);
	}

	@Override
	public boolean evaluate(String value) {
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
