
package org.mgnl.nicki.shop.base.objects;


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


/**
 * The Interface MultipleInstancesCatalogArticle.
 */
public interface MultipleInstancesCatalogArticle {

	/**
	 * Checks if is multiple.
	 *
	 * @return true, if is multiple
	 */
	boolean isMultiple();

	/**
	 * Gets the catalog value provider.
	 *
	 * @return the catalog value provider
	 */
	CatalogValueProvider getCatalogValueProvider();
	
}
