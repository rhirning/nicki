package org.mgnl.nicki.shop.rules;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class RuleQuery {
	private String query = null;
	private String baseDn = null;
	private boolean needQuery = false;
	
	public void setQuery(String query) {
		this.query = query;
		if (StringUtils.isNotEmpty(query)) {
			needQuery = true;
		}
	}
	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
		needQuery = true;
	}
	public boolean isNeedQuery() {
		return needQuery;
	}
	public String getQuery() {
		return query;
	}
	public String getBaseDn() {
		return baseDn;
	}
	public String toString() {
		return baseDn + ":" + query;
	}
}
