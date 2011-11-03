package org.mgnl.nicki.shop.catalog;

import java.util.ArrayList;
import java.util.List;

import org.mgnl.nicki.dynamic.objects.objects.Role;

@SuppressWarnings("serial")
public class RoleArticle extends CatalogArticle {
	private Role role;
	private CatalogPage page;

	public RoleArticle(Role role, CatalogPage page) {
		this.role = role;
		this.page = page;
	}

	@Override
	public TYPE getArticleType() {
		return TYPE.ROLE;
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
	public String getPath() {
		return role.getPath();
	}

	@Override
	public String getName() {
		return role.getPath();
	}

	@Override
	public String getDisplayName() {
		return role.getDisplayName();
	}
	
	@Override
	public String getCatalogPath() {
		return page.getCatalogPath() + Catalog.PATH_SEPARATOR + getName();
	}

	@Override
	public String getRolePath() {
		return role.getPath();
	}



}
