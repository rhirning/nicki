/**
 * Copyright (c) 2003-2015 Dr. Ralf Hirning
 * All rights reserved.
 *  
 * This program is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 * 
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU Public License v3.0
 * which is available at
 * http://www.gnu.org/licenses/gpl.html
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 * 
 * Any modifications to this file must keep this entire header
 * intact.
*/
package org.mgnl.nicki.shop.objects;

import java.util.ArrayList;
import java.util.List;

import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.context.AppContext;
import org.mgnl.nicki.core.context.DynamicObjectFactory;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;

public class CatalogArticleFactory {
	private static CatalogArticleFactory instance;
	private List<CatalogArticle> articles = new ArrayList<CatalogArticle>();
	
	public static CatalogArticleFactory getInstance() throws InvalidPrincipalException, InstantiateDynamicObjectException {
		if (instance == null) {
			instance = new CatalogArticleFactory();
		}
		return instance;
	}

	public CatalogArticleFactory() throws InvalidPrincipalException, InstantiateDynamicObjectException {
		DynamicObjectFactory objectFactory = AppContext.getSystemContext().getObjectFactory();
		for (CatalogArticle catalogArticle : objectFactory.findDynamicObjects(CatalogArticle.class)) {
			articles.add(catalogArticle);
		}
	}

	public List<CatalogArticle> getArticles() {
		return articles;
	}
	
	
}
