package org.mgnl.nicki.ldap.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SearchResultEntry implements Serializable {
	private static final long serialVersionUID = -6784848780254677616L;
	private Map<String, Object> values = new HashMap<String, Object>();
	private String dn;
	
	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public void addValue(String key, Object value) {
		this.values.put(key, value);
	}
	
	public Object getValue(String key) {
		return this.values.get(key);
	}

	public Map<String, Object> getValues() {
		return values;
	}
	
}
