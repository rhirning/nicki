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
