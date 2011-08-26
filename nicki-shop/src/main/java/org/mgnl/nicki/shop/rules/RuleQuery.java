package org.mgnl.nicki.shop.rules;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class RuleQuery {
	private String query = null;
	private List<BaseDn> baseDns = new ArrayList<BaseDn>();
	private boolean needQuery = false;
	
	public void setQuery(String query) {
		this.query = query;
		if (StringUtils.isNotEmpty(query)) {
			needQuery = true;
		}
	}
	public void addBaseDn(BaseDn baseDn) {
		this.baseDns.add(baseDn);
		needQuery = true;
	}
	public boolean isNeedQuery() {
		return needQuery;
	}
	public String getQuery() {
		return query;
	}
	public List<BaseDn> getBaseDns() {
		return baseDns;
	}
	public String toString() {
		return baseDns + query;
	}
}
