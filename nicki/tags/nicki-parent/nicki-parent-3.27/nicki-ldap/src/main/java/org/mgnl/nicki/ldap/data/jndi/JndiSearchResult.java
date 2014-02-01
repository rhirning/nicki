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
package org.mgnl.nicki.ldap.data.jndi;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchResult;

import org.mgnl.nicki.core.objects.ContextAttributes;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JndiSearchResult implements ContextSearchResult {
	Logger LOG = LoggerFactory.getLogger(JndiSearchResult.class);
	private SearchResult rs;

	public JndiSearchResult(SearchResult rs) {
		this.rs = rs;
	}

	public String getNameInNamespace() {
		return rs.getNameInNamespace();
	}

	public ContextAttributes getAttributes() {		
		return new JndiAttributes(rs.getAttributes());
	}

	@Override
	public Object getValue(String name) {
		try {
			return rs.getAttributes().get(name).get();
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public List<Object> getValues(String name) {
		List<Object> list = new ArrayList<Object>();
		try {
			for (NamingEnumeration<?> iterator = rs.getAttributes().get(name).getAll(); iterator.hasMoreElements();) {
				list.add(iterator.next());
			}
		} catch (Exception e) {
			// nothing to do
		}
		return list;
	}

}
