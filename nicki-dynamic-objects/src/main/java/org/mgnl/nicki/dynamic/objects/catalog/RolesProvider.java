package org.mgnl.nicki.dynamic.objects.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.dynamic.objects.objects.CatalogArticle;
import org.mgnl.nicki.dynamic.objects.objects.CatalogPage;
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
		return page.getContext().loadObject(CatalogArticle.class, key);
	}

	@Override
	public void init(CatalogPage catalogPage) {
		this.page = catalogPage;
	}

}
