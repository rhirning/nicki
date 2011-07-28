package org.mgnl.nicki.dynamic.objects.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.dynamic.objects.objects.CatalogArticle;
import org.mgnl.nicki.dynamic.objects.objects.CatalogPage;
import org.mgnl.nicki.dynamic.objects.objects.Resource;

@SuppressWarnings("serial")
public class ResourcesProvider implements Provider, Serializable {
	CatalogPage page = null;
	@Override
	public List<CatalogArticle> getArticles(CatalogPage catalogPage) {
		List<CatalogArticle> articles = new ArrayList<CatalogArticle>();
		List<Resource> resources = catalogPage.getContext().loadObjects(Resource.class,
				Config.getProperty("nicki.resources.basedn"), null);
		if (resources != null && resources.size() > 0) {
			for (Iterator<Resource> iterator = resources.iterator(); iterator.hasNext();) {
				Resource resource = iterator.next();
				articles.add(new ResourceArticle(resource, catalogPage));
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
