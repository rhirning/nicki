/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.helper;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.objects.ContextAttribute;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;

public class LdapHelper {

	public enum LOGIC {

		AND("&"),
		OR("|");
		private String sign;

		LOGIC(String sign) {
			this.sign = sign;
		}

		;

		public String getSign() {
			return this.sign;
		}
	}

	public static boolean isPathEqual(String refPath, String comparePath) {
		if (StringUtils.equalsIgnoreCase(refPath, comparePath)) {
			return true;
		}

		return false;
	}

	public static List<Object> getAttributes(ContextSearchResult rs, String attributeName) {
		List<Object> attributeList = new ArrayList<Object>();

		try {
			ContextAttribute attr = rs.getAttributes().get(attributeName);
			if (attr != null) {
				for (Enumeration<Object> vals = (Enumeration<Object>) attr.getAll(); vals.hasMoreElements();) {
					attributeList.add(vals.nextElement());
				}
			}
		} catch (Exception e) {
		}
		return attributeList;
	}

	public static Object getAttribute(ContextSearchResult rs, String attributeName) {
		try {
			ContextAttribute attr = rs.getAttributes().get(attributeName);
			if (attr != null) {
				Enumeration<?> vals = attr.getAll();
				if (vals.hasMoreElements()) {
					return vals.nextElement();
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

	public static String getPath(String parentPath, String namingLdapAttribute, String namingValue) {
		StringBuffer sb = new StringBuffer();
		sb.append(StringUtils.upperCase(namingLdapAttribute));
		sb.append("=");
		sb.append(namingValue);
		sb.append(",");
		sb.append(parentPath);

		return sb.toString();
	}

	public static String getParentPath(String path) {
		return StringUtils.strip(StringUtils.substringAfter(path, ","));
	}

	public static String getNamingValue(String path) {
		return StringUtils.strip(StringUtils.substringAfter(StringUtils.substringBefore(path, ","), "="));
	}

	public static void negateQuery(StringBuffer sb) {
		if (sb.length() > 0) {
			sb.insert(0, "!");
			sb.insert(0, "(");
			sb.append(")");
		}
	}

	public static void addQuery(StringBuffer sb, String query, LOGIC andOr) {
		if (sb.length() == 0) {
			sb.append(query);
			if (!StringUtils.startsWith(query, "(")) {
				sb.insert(0,"(");
				sb.append(")");
			}
		} else {
			sb.insert(0, andOr.getSign());
			sb.insert(0, "(");
			if (!StringUtils.startsWith(query, "(")) {
				sb.append("(");
			}
			sb.append(query);
			if (!StringUtils.startsWith(query, "(")) {
				sb.append(")");
			}
			sb.append(")");
		}
	}
	
	public static String getSlashPath(String parentPath, String childPath) {
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotEmpty(parentPath)) {
			if (StringUtils.equals(parentPath, childPath)) {
				return "/";
			}
			childPath = StringUtils.substringBeforeLast(childPath, "," + parentPath);
		}
		
		String parts[] = StringUtils.split(childPath, ",");
		if (parts != null) {
			for (int i = parts.length - 1; i >=0; i--) {
				String part = StringUtils.substringAfter(parts[i], "=");
				sb.append("/");
				sb.append(part);
			}
		}
		return sb.toString();
	}


}
