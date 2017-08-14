package org.mgnl.nicki.shop.base.visitor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.shop.base.objects.Catalog;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.objects.CatalogObject;
import org.mgnl.nicki.shop.base.objects.CatalogPage;

public class FindPermissionDnVisitor implements CatalogVisitor {

	private String permissionDn;
	private List<CatalogArticle> catalogArticles = new ArrayList<CatalogArticle>();
	
	public FindPermissionDnVisitor(String permissionDn) {
		super();
		this.permissionDn = permissionDn;
	}

	@Override
	public ACTION visit(CatalogObject catalogObject) {
		return ACTION.CONTINUE;
	}

	@Override
	public ACTION visit(Catalog catalog) {
		return ACTION.CONTINUE;
	}

	@Override
	public ACTION visit(CatalogPage catalogPage) {
		for (CatalogArticle catalogArticle : catalogPage.getArticles()) {
			if (StringUtils.equals(permissionDn, catalogArticle.getPermissionDn())) {
				catalogArticles.add(catalogArticle);
			}
		}
		return ACTION.CONTINUE;
	}

	@Override
	public ACTION visit(CatalogArticle catalogArticle) {
		return ACTION.CONTINUE;
	}

	public List<CatalogArticle> getCatalogArticles() {
		return catalogArticles;
	}

}
