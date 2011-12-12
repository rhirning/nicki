/**
 * This file Copyright (c) 2011 deron Consulting GmbH
 * Author: Dr. Ralf Hirning
 * All rights reserved.
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

	public CatalogArticle getArticle(String key) {
		Role role = this.page.getContext().loadObject(Role.class, key);
		if (role != null) {
			return  new RoleArticle(role, this.page);
		}
		return null;
	}

	public void init(CatalogPage catalogPage) {
		this.page = catalogPage;
	}

}
