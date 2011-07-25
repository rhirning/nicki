/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mgnl.nicki.dynamic.objects.objects;

import java.util.HashMap;
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;

/**
 *
 * @author cna
 */
@SuppressWarnings("serial")
public class LdapSearchGroup extends DynamicObject {

	public enum SEARCHSCOPE {

		SUBORDINATES("base"),
		OBJECT("one"),
		SUBTREE("sub");
		private String value;
		private static final HashMap<String, SEARCHSCOPE> map = new HashMap<String, SEARCHSCOPE>();

		static {
			map.put("base", SUBORDINATES);
			map.put("sub", SUBTREE);
			map.put("one", OBJECT);
		}

		;

		private SEARCHSCOPE(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public static SEARCHSCOPE fromValue(String type) {
			return map.get(type);
		}
	};
	private String ldapSearch = "";
	private SEARCHSCOPE searchScope;
	private String searchRoot = "";
	private boolean initialized;

	@Override
	public void initDataModel() {
		addObjectClass("dynamicGroup");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("member", "member", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey();
		dynAttribute.setReadonly();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("query", "memberQueryURL", String.class);
		addAttribute(dynAttribute);
	}

	private void load() {
		if (initialized != true && StringUtils.isNotEmpty((String) get("query"))) {
			String memberQuery = (String) get("query");

			searchRoot = StringUtils.substringBetween(memberQuery, "///", "??");
			searchScope = SEARCHSCOPE.fromValue(StringUtils.substringBetween(memberQuery, "??", "?"));
			ldapSearch = StringUtils.substringAfterLast(memberQuery, "?");

			initialized = true;
		}
	}

	private void reload() {
		String memberQuery = (String) get("query");

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

	public void setQueryURL(String memberQuery) {
		put("query", memberQuery);
		reload();
	}

	public String getQuery() {
		load();
		return ldapSearch;
	}

	public void setQueryComponents(String searchRoot, SEARCHSCOPE searchScope, String ldapSearch) {
		if (StringUtils.isNotBlank(searchRoot) && StringUtils.isNotBlank(ldapSearch) && null != searchScope) {
			this.ldapSearch = ldapSearch;
			this.searchRoot = searchRoot;
			this.searchScope = searchScope;

			put("query", getQueryURL());
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
}
