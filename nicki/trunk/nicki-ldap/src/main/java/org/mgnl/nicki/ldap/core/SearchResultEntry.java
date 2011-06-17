package org.mgnl.nicki.ldap.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SearchResultEntry implements Serializable {
	private static final long serialVersionUID = -6784848780254677616L;
	private Map<String, String> values = new HashMap<String, String>();
	private String dn;
	
	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public void addValue(String key, String value) {
		this.values.put(key, value);
	}
	
	public String getValue(String key) {
		return this.values.get(key);
	}

	public Map<String, String> getValues() {
		return values;
	}
	
}
