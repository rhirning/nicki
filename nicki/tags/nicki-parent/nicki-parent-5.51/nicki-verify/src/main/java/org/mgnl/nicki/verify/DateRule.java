package org.mgnl.nicki.verify;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.i18n.I18n;

public class DateRule extends Rule {
	private static final long serialVersionUID = 1L;
	public static final String dateFormat = "dd.MM.yyyy";
	public static final SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

	@Override
	public boolean evaluate(String value, Map<String, String> values) {
		try {
			synchronized (formatter) {
				formatter.parse(value);
			}
			String[] parts = StringUtils.split(value, ".");
			Integer day = Integer.parseInt(parts[0]);
			if (day < 1 || day > 31) {
				return false;
			}
			Integer month = Integer.parseInt(parts[1]);
			if (month < 1 || month > 12) {
				return false;
			}
			Integer year = Integer.parseInt(parts[2]);
			int now = Calendar.getInstance().get(Calendar.YEAR);
			if (year < now - 200 || year > now + 200) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
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