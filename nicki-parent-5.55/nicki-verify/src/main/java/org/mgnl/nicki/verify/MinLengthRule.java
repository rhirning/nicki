package org.mgnl.nicki.verify;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.i18n.I18n;

public class MinLengthRule extends Rule {
	private static final long serialVersionUID = 1L;
	private int length = 0;

	public MinLengthRule(String value) {
		setParameter(value);
		this.length = Integer.parseInt(value);
	}

	@Override
	public boolean evaluate(String value, Map<String, String> values) {
		return StringUtils.length(value) >= this.length;
	}
	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".minlength", getParameter());
	}
	public String toString() {
		return "minLength:" + length;
	}


}
