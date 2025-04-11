
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


import org.mgnl.nicki.core.visitor.Visitor;
import org.mgnl.nicki.shop.base.objects.Catalog;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.objects.CatalogObject;
import org.mgnl.nicki.shop.base.objects.CatalogPage;

// TODO: Auto-generated Javadoc
/**
 * The Interface CatalogVisitor.
 */
public interface CatalogVisitor extends Visitor {

	/**
	 * Visit.
	 *
	 * @param catalogObject the catalog object
	 * @return the visitor. ACTION
	 */
	Visitor.ACTION visit(CatalogObject catalogObject); 
	
	/**
	 * Visit.
	 *
	 * @param catalog the catalog
	 * @return the visitor. ACTION
	 */
	Visitor.ACTION visit(Catalog catalog); 
	
	/**
	 * Visit.
	 *
	 * @param catalogPage the catalog page
	 * @return the visitor. ACTION
	 */
	Visitor.ACTION visit(CatalogPage catalogPage);
	
	/**
	 * Visit.
	 *
	 * @param catalogArticle the catalog article
	 * @return the visitor. ACTION
	 */
	Visitor.ACTION visit(CatalogArticle catalogArticle); 
}
