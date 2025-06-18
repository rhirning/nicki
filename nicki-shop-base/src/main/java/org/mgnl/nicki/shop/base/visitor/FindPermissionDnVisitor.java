
package org.mgnl.nicki.shop.base.visitor;

/*-
 * #%L
 * nicki-shop-base
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.shop.base.objects.Catalog;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.objects.CatalogObject;
import org.mgnl.nicki.shop.base.objects.CatalogPage;


/**
 * The Class FindPermissionDnVisitor.
 */
public class FindPermissionDnVisitor implements CatalogVisitor {

	/** The permission dn. */
	private String permissionDn;
	
	/** The catalog articles. */
	private List<CatalogArticle> catalogArticles = new ArrayList<CatalogArticle>();
	
	/**
	 * Instantiates a new find permission dn visitor.
	 *
	 * @param permissionDn the permission dn
	 */
	public FindPermissionDnVisitor(String permissionDn) {
		super();
		this.permissionDn = permissionDn;
	}

	/**
	 * Visit.
	 *
	 * @param catalogObject the catalog object
	 * @return the action
	 */
	@Override
	public ACTION visit(CatalogObject catalogObject) {
		return ACTION.CONTINUE;
	}

	/**
	 * Visit.
	 *
	 * @param catalog the catalog
	 * @return the action
	 */
	@Override
	public ACTION visit(Catalog catalog) {
		return ACTION.CONTINUE;
	}

	/**
	 * Visit.
	 *
	 * @param catalogPage the catalog page
	 * @return the action
	 */
	@Override
	public ACTION visit(CatalogPage catalogPage) {
		for (CatalogArticle catalogArticle : catalogPage.getArticles()) {
			if (StringUtils.equals(permissionDn, catalogArticle.getPermissionDn())) {
				catalogArticles.add(catalogArticle);
			}
		}
		return ACTION.CONTINUE;
	}

	/**
	 * Visit.
	 *
	 * @param catalogArticle the catalog article
	 * @return the action
	 */
	@Override
	public ACTION visit(CatalogArticle catalogArticle) {
		return ACTION.CONTINUE;
	}

	/**
	 * Gets the catalog articles.
	 *
	 * @return the catalog articles
	 */
	public List<CatalogArticle> getCatalogArticles() {
		return catalogArticles;
	}

}
