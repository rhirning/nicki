
package org.mgnl.nicki.dynamic.objects.objects;

/*-
 * #%L
 * nicki-dynamic-objects
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.objects.BaseDynamicObject;


/**
 * The Class LdapSearchGroup.
 *
 * @author cna
 */
@SuppressWarnings("serial")
@DynamicObject
@ObjectClass("dynamicGroup")
public class LdapSearchGroup extends BaseDynamicObject {
	
	/** The Constant ATTRIBUTE_MEMBER. */
	public static final String ATTRIBUTE_MEMBER = "member";
	
	/** The Constant ATTRIBUTE_MEMBER_QUERY. */
	public static final String ATTRIBUTE_MEMBER_QUERY = "memberQuery";

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	@DynamicAttribute(externalName="cn", naming=true)
	public String getName() {
		return super.getName();
	}

	/**
	 * The Enum SEARCHSCOPE.
	 */
	public enum SEARCHSCOPE {

		/** The subordinates. */
		SUBORDINATES("base"),
		
		/** The object. */
		OBJECT("one"),
		
		/** The subtree. */
		SUBTREE("sub");
		
		/** The value. */
		private String value;
		
		/** The Constant MAP. */
		private static final HashMap<String, SEARCHSCOPE> MAP = new HashMap<String, SEARCHSCOPE>();

		static {
			MAP.put("base", SUBORDINATES);
			MAP.put("sub", SUBTREE);
			MAP.put("one", OBJECT);
		}

		;

		/**
		 * Instantiates a new searchscope.
		 *
		 * @param value the value
		 */
		private SEARCHSCOPE(String value) {
			this.value = value;
		}

		/**
		 * Gets the value.
		 *
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * From value.
		 *
		 * @param type the type
		 * @return the searchscope
		 */
		public static SEARCHSCOPE fromValue(String type) {
			return MAP.get(type);
		}
	};
	
	/** The ldap search. */
	private String ldapSearch = "";
	
	/** The search scope. */
	private SEARCHSCOPE searchScope;
	
	/** The search root. */
	private String searchRoot = "";
	
	/** The initialized. */
	private boolean initialized;


	/**
	 * Load.
	 */
	private void load() {
		if ((initialized != true) && StringUtils.isNotEmpty((String) getMemberQuery())) {
			String memberQuery = (String) getMemberQuery();

			searchRoot = StringUtils.substringBetween(memberQuery, "///", "??");
			searchScope = SEARCHSCOPE.fromValue(StringUtils.substringBetween(memberQuery, "??", "?"));
			ldapSearch = StringUtils.substringAfterLast(memberQuery, "?");

			initialized = true;
		}
	}

	/**
	 * Gets the member.
	 *
	 * @return the member
	 */
	@SuppressWarnings("unchecked")
	@DynamicAttribute(externalName = "member", readonly=true, foreignKey=Person.class)
	public List<String> getMember() {
		return (List<String>) get(ATTRIBUTE_MEMBER);
	}

	/**
	 * Reload.
	 */
	private void reload() {
		String memberQuery = (String) getMemberQuery();

		searchRoot = StringUtils.substringBetween(memberQuery, "///", "??");
		searchScope = SEARCHSCOPE.valueOf(StringUtils.upperCase(StringUtils.substringBetween(memberQuery, "??", "?")));
		ldapSearch = StringUtils.substringAfterLast(memberQuery, "?");

		initialized = true;
	}

	/**
	 * Gets the query URL.
	 *
	 * @return the query URL
	 */
	public String getQueryURL() {
		load();
		if (initialized) {
			return "ldap:///" + searchRoot + "??" + searchScope.getValue() + "?" + ldapSearch;
		} else {
			return null;
		}

	}

	/**
	 * Sets the query URL.
	 *
	 * @param memberQuery the new query URL
	 */
	@Deprecated
	public void setQueryURL(String memberQuery) {
		setMemberQuery(memberQuery);
		reload();
	}
	
	/**
	 * Gets the query.
	 *
	 * @return the query
	 */
	@Deprecated
	public String getQuery() {
		load();
		return ldapSearch;
	}

	/**
	 * Sets the query components.
	 *
	 * @param searchRoot the search root
	 * @param searchScope the search scope
	 * @param ldapSearch the ldap search
	 */
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

	/**
	 * Gets the search scope.
	 *
	 * @return the search scope
	 */
	public SEARCHSCOPE getSearchScope() {
		load();
		return searchScope;
	}

	/**
	 * Gets the search root.
	 *
	 * @return the search root
	 */
	public String getSearchRoot() {
		load();
		return searchRoot;
	}

	/**
	 * Gets the member query.
	 *
	 * @return the member query
	 */
	@DynamicAttribute(externalName="memberQueryURL")
	public String getMemberQuery() {
		return getAttribute(ATTRIBUTE_MEMBER_QUERY);
	}

	/**
	 * Sets the member query.
	 *
	 * @param memberQuery the new member query
	 */
	public void setMemberQuery(String memberQuery) {
		this.put(ATTRIBUTE_MEMBER_QUERY, memberQuery);
	}
}
