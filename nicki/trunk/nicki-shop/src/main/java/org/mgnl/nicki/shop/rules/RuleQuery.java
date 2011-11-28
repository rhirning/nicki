/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
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
