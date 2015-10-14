package org.mgnl.nicki.verify;

import org.mgnl.nicki.core.i18n.I18n;

public class NumberRule extends Rule {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean evaluate(String value) {
		try {
			getNumber(value);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".number");
	}
	public String toString() {
		return "number";
	}



}
