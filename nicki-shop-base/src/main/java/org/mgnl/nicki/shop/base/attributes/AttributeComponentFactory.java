
package org.mgnl.nicki.shop.base.attributes;

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

import lombok.extern.slf4j.Slf4j;


/**
 * A factory for creating AttributeComponent objects.
 */
@Slf4j
public class AttributeComponentFactory {

	/**
	 * Gets the attribute component.
	 *
	 * @param <T> the generic type
	 * @param type the type
	 * @return the attribute component
	 */
	@SuppressWarnings("unchecked")
	static public <T extends Object> BaseAttributeComponent<T> getAttributeComponent(String type) {
		try {
			Component component = Component.valueOf(type);
			if (component == null) {
				component = Component.DEFAULT;
			}
			return (BaseAttributeComponent<T>) component.getInstance();
		} catch (Exception e) {
			log.error("Error", e);
		}
		return null;
	}

}
