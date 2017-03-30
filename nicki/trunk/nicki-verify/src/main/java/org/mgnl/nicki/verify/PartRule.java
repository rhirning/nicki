package org.mgnl.nicki.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrTokenizer;
import org.mgnl.nicki.core.i18n.I18n;

public class PartRule extends Rule {
	private String separator = "|";
	private List<List<Integer>> list = new ArrayList<>();
	
	private static final long serialVersionUID = 1L;

	public PartRule(String parameter) {
		setParameter(parameter);
		if (StringUtils.contains(parameter, ":")) {
			separator = StringUtils.substringBefore(parameter, ":");
			parameter = StringUtils.substringAfter(parameter, ":");
		}
		
		for (String entry : StringUtils.split(parameter, ",")) {
			List<Integer> listEntry = new ArrayList<>();
			list.add(listEntry);
			for (int i = 0; i < entry.length(); i++) {
				String pos = StringUtils.substring(entry, i, i+1);
				Integer value = Integer.parseUnsignedInt(pos);
				listEntry.add(value);
			}
		}
	}

	@Override
	public boolean evaluate(String value, Map<String, String> values) {
		StrTokenizer tokenizer = new StrTokenizer(value, separator);
		tokenizer.setIgnoreEmptyTokens(false);
		String parts[] = tokenizer.getTokenArray();
		
		for (List<Integer> listEntry : list) {
			boolean pass = true;
			for (int i = 0; i < parts.length; i++) {
				if (StringUtils.isBlank(parts[i]) && listEntry.contains(i)) {
					pass = false;
					break;
				} else if (StringUtils.isNotBlank(parts[i]) && !listEntry.contains(i)) {
					pass = false;
					break;
				}
			}
			if (pass) {
				return true;
			}
			
		}
		return false;
	}
	
	@Override
	public String getMessage() {
		return I18n.getText(getI18nBase() + ".part");
	}
	public String toString() {
		return "part:" + list;
	}



}
