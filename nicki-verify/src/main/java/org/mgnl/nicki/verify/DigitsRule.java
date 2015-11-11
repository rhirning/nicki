package org.mgnl.nicki.verify;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.i18n.I18n;

public class DigitsRule extends Rule {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean evaluate(String value, Map<String, String> values) {
		return StringUtils.isNumeric(value);
	}

	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".digits");
	}
	public String toString() {
		return "digits";
	}

}
