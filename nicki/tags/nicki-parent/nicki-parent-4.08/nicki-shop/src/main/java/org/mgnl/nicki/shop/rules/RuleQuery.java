/**
 * Copyright (c) 2003-2015 Dr. Ralf Hirning
 * All rights reserved.
 *  
 * This program is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 * 
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU Public License v3.0
 * which is available at
 * http://www.gnu.org/licenses/gpl.html
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 * 
 * Any modifications to this file must keep this entire header
 * intact.
*/
package org.mgnl.nicki.shop.rules;

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
