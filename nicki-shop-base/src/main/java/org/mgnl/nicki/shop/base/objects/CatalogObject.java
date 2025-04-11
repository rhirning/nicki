
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


import java.util.List;

import org.mgnl.nicki.core.objects.BaseDynamicObject;
import org.mgnl.nicki.core.visitor.Visitor;
import org.mgnl.nicki.shop.base.visitor.CatalogVisitor;


// TODO: Auto-generated Javadoc
/**
 * The Class CatalogObject.
 */
public abstract class CatalogObject extends BaseDynamicObject {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1244940179441748075L;

	/**
	 * Accept.
	 *
	 * @param visitor the visitor
	 * @return the visitor. ACTION
	 */
	public Visitor.ACTION accept(CatalogVisitor visitor) {
		if (visitor.visit(this) == Visitor.ACTION.FINISH) {
			return Visitor.ACTION.FINISH;
		} else {
			if (getChildList() != null) {
				for (CatalogObject child : getChildList()) {
					if (child.accept(visitor) == Visitor.ACTION.FINISH) {
						return Visitor.ACTION.FINISH;
					}
				}
			}
			return Visitor.ACTION.CONTINUE;
		}
		
	}

	/**
	 * Gets the child list.
	 *
	 * @return the child list
	 */
	public abstract List<? extends CatalogObject> getChildList();

}
