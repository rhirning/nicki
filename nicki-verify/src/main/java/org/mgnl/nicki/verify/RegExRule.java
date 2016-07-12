package org.mgnl.nicki.verify;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mgnl.nicki.core.i18n.I18n;

public class RegExRule extends Rule {
	private static final long serialVersionUID = 1L;
	Pattern pattern;

	public RegExRule(String parameter) {
		setParameter(parameter);
		pattern = Pattern.compile(parameter);
	}

	@Override
	public boolean evaluate(String value, Map<String, String> values) {

		Matcher matcher = pattern.matcher(value);
		//return (StringUtils.equals(value, matcher.group()));
		return matcher.matches();
	}
	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".regex", getParameter());
	}
	public String toString() {
		return "regex:" + getParameter();
	}



}
