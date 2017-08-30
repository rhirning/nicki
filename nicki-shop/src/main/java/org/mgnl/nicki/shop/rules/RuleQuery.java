/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.shop.rules;

import org.apache.commons.lang.StringUtils;

public class RuleQuery {
	private String query;
	private String baseDn;
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
