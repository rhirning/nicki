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
package org.mgnl.nicki.dynamic.objects.objects;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.objects.BaseDynamicObject;

/**
 *
 * @author cna
 */
@SuppressWarnings("serial")
@DynamicObject
@ObjectClass("dynamicGroup")
public class LdapSearchGroup extends BaseDynamicObject {
	public static final String ATTRIBUTE_MEMBER = "member";
	public static final String ATTRIBUTE_MEMBER_QUERY = "memberQuery";

	@Override
	@DynamicAttribute(externalName="cn", naming=true)
	public String getName() {
		return super.getName();
	}

	public enum SEARCHSCOPE {

		SUBORDINATES("base"),
		OBJECT("one"),
		SUBTREE("sub");
		private String value;
		private static final HashMap<String, SEARCHSCOPE> MAP = new HashMap<String, SEARCHSCOPE>();

		static {
			MAP.put("base", SUBORDINATES);
			MAP.put("sub", SUBTREE);
			MAP.put("one", OBJECT);
		}

		;

		private SEARCHSCOPE(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public static SEARCHSCOPE fromValue(String type) {
			return MAP.get(type);
		}
	};
	private String ldapSearch = "";
	private SEARCHSCOPE searchScope;
	private String searchRoot = "";
	private boolean initialized;


	private void load() {
		if ((initialized != true) && StringUtils.isNotEmpty((String) getMemberQuery())) {
			String memberQuery = (String) getMemberQuery();

			searchRoot = StringUtils.substringBetween(memberQuery, "///", "??");
			searchScope = SEARCHSCOPE.fromValue(StringUtils.substringBetween(memberQuery, "??", "?"));
			ldapSearch = StringUtils.substringAfterLast(memberQuery, "?");

			initialized = true;
		}
	}

	@SuppressWarnings("unchecked")
	@DynamicAttribute(externalName = "member", readonly=true, foreignKey=Person.class)
	public List<String> getMember() {
		return (List<String>) get(ATTRIBUTE_MEMBER);
	}

	private void reload() {
		String memberQuery = (String) getMemberQuery();

		searchRoot = StringUtils.substringBetween(memberQuery, "///", "??");
		searchScope = SEARCHSCOPE.valueOf(StringUtils.upperCase(StringUtils.substringBetween(memberQuery, "??", "?")));
		ldapSearch = StringUtils.substringAfterLast(memberQuery, "?");

		initialized = true;
	}

	public String getQueryURL() {
		load();
		if (initialized) {
			return "ldap:///" + searchRoot + "??" + searchScope.getValue() + "?" + ldapSearch;
		} else {
			return null;
		}

	}

	@Deprecated
	public void setQueryURL(String memberQuery) {
		setMemberQuery(memberQuery);
		reload();
	}
	
	@Deprecated
	public String getQuery() {
		load();
		return ldapSearch;
	}

	public void setQueryComponents(String searchRoot, SEARCHSCOPE searchScope, String ldapSearch) {
		if (StringUtils.isNotBlank(searchRoot) && StringUtils.isNotBlank(ldapSearch) && (null != searchScope)) {
			this.ldapSearch = ldapSearch;
			this.searchRoot = searchRoot;
			this.searchScope = searchScope;

			setMemberQuery(getQueryURL());
			initialized = true;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public SEARCHSCOPE getSearchScope() {
		load();
		return searchScope;
	}

	public String getSearchRoot() {
		load();
		return searchRoot;
	}

	@DynamicAttribute(externalName="memberQueryURL")
	public String getMemberQuery() {
		return getAttribute(ATTRIBUTE_MEMBER_QUERY);
	}

	public void setMemberQuery(String memberQuery) {
		this.put(ATTRIBUTE_MEMBER_QUERY, memberQuery);
	}
}
