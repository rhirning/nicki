package org.mgnl.nicki.verify;

import java.text.SimpleDateFormat;

import org.mgnl.nicki.core.i18n.I18n;

public class DateRule extends Rule {
	private static final long serialVersionUID = 1L;
	public static final String dateFormat = "dd.MM.yyyy";
	public static final SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

	@Override
	public boolean evaluate(String value) {
		try {
			formatter.parse(value);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".date");
	}

	public String toString() {
		return "date";
	}
}
