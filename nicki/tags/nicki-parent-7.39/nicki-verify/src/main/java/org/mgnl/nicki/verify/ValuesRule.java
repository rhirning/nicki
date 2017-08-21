package org.mgnl.nicki.verify;

import java.util.List;
import java.util.Map;

import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.i18n.I18n;

// val					= values:value1,value2,value3

@SuppressWarnings("serial")
public class ValuesRule extends Rule {
	static final public String SEPARATOR = ",";
	private List<String> allowedValues;

	public ValuesRule(String parameter) {
		setParameter(parameter);
		allowedValues = DataHelper.getList(parameter, SEPARATOR);
	}
	
	@Override
	public boolean evaluate(String value, Map<String, String> values) {
		if (allowedValues.contains(value)) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".values", getParameter());
	}
	public String toString() {
		return "values:" + getParameter();
	}



}
