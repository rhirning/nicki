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
package org.mgnl.nicki.shop.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.dynamic.objects.objects.Role;

@SuppressWarnings("serial")
public class RolesProvider implements Provider, Serializable {
	CatalogPage page = null;
	@Override
	public List<CatalogArticle> getArticles(CatalogPage catalogPage) {
		List<CatalogArticle> articles = new ArrayList<CatalogArticle>();
		List<Role> roles = catalogPage.getContext().loadObjects(Role.class,
				Config.getProperty("nicki.roles.basedn"), null);
		if (roles != null && roles.size() > 0) {
			for (Iterator<Role> iterator = roles.iterator(); iterator.hasNext();) {
				Role role = iterator.next();
				articles.add(new RoleArticle(role, catalogPage));
			}
		}
		return articles;
	}

	@Override
	public CatalogArticle getArticle(String key) {
		Role role = this.page.getContext().loadObject(Role.class, key);
		if (role != null) {
			return  new RoleArticle(role, this.page);
		}
		return null;
	}

	@Override
	public void init(CatalogPage catalogPage) {
		this.page = catalogPage;
	}

}
