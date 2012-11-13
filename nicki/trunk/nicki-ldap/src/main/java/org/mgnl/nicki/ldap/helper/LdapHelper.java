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
package org.mgnl.nicki.ldap.helper;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.objects.ContextAttribute;
import org.mgnl.nicki.core.objects.ContextSearchResult;

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
