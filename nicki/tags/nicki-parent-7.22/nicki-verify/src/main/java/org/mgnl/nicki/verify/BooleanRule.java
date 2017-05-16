package org.mgnl.nicki.verify;

import java.util.Map;

import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.i18n.I18n;

public class BooleanRule extends Rule {
	private static final long serialVersionUID = 1L;
	Boolean booleanValue;

	public BooleanRule(String parameter) {
		setParameter(parameter);
		booleanValue = DataHelper.booleanOf(parameter);
	}

	@Override
	public boolean evaluate(String value, Map<String, String> values) {
		if (booleanValue != null) {
			try {
				return booleanValue == DataHelper.booleanOf(value);
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}
	
	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".boolean", getParameter());
	}
	public String toString() {
		return "boolean:" + booleanValue;
	}



}
