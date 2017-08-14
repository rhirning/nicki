package org.mgnl.nicki.verify;

import org.mgnl.nicki.core.i18n.I18n;

public class RequiredRule extends MinLengthRule {
	private static final long serialVersionUID = 1L;

	public RequiredRule() {
		super("1");
	}

	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".required");
	}

	public String toString() {
		return "required";
	}

}
