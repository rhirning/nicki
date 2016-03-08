package org.mgnl.nicki.verify;

import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.i18n.I18n;

public class EmailRule extends Rule {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean evaluate(String value, Map<String, String> values) {
		try {
			new InternetAddress(value);
			if (!hasNameAndDomain(value)) {
				return false;
			}
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	private static boolean hasNameAndDomain(String aEmailAddress) {
		String[] tokens = aEmailAddress.split("@");
		if (tokens.length != 2) return false;
		
		return StringUtils.isNotEmpty(tokens[0])
			&& StringUtils.split(tokens[1], ".").length > 1;
	}

	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".email");
	}
	public String toString() {
		return "email";
	}


}
