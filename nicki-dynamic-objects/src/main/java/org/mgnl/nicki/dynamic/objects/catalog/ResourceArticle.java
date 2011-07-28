package org.mgnl.nicki.dynamic.objects.catalog;

import java.util.ArrayList;
import java.util.List;

import org.mgnl.nicki.dynamic.objects.objects.CatalogArticle;
import org.mgnl.nicki.dynamic.objects.objects.CatalogArticleAttribute;
import org.mgnl.nicki.dynamic.objects.objects.CatalogPage;
import org.mgnl.nicki.dynamic.objects.objects.Resource;

@SuppressWarnings("serial")
public class ResourceArticle extends CatalogArticle {
	private Resource resource;
	private CatalogPage page;

	public ResourceArticle(Resource resource, CatalogPage page) {
		this.resource = resource;
		this.page = page;
	}

	@Override
	public TYPE getArticleType() {
		return TYPE.RESOURCE;
	}

	@Override
	public List<CatalogArticleAttribute> getAttributes() {
		return new ArrayList<CatalogArticleAttribute>();
	}

	@Override
	public List<CatalogArticleAttribute> getInheritedAttributes() {
		return page.getAttributes();
	}

	@Override
	public List<CatalogArticleAttribute> getAllAttributes() {
		return getInheritedAttributes();
	}

	@Override
	public String getName() {
		return resource.getPath();
	}

	@Override
	public String getDisplayName() {
		return resource.getDisplayName();
	}

}
