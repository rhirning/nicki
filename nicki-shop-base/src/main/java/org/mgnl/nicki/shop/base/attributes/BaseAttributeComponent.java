
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


/**
 * The Interface BaseAttributeComponent.
 *
 * @param <F> the generic type
 */
public interface BaseAttributeComponent<F> {

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	void setValue(F value);
	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	F getValue();
	
	/**
	 * Sets the caption.
	 *
	 * @param caption the new caption
	 */
	void setCaption(String caption);
	
	/**
	 * Sets the enabled.
	 *
	 * @param enabled the new enabled
	 */
	void setEnabled(boolean enabled);
	
	/**
	 * Checks if is enabled.
	 *
	 * @return true, if is enabled
	 */
	boolean isEnabled();
	
	/**
	 * Gets the string value.
	 *
	 * @param value the value
	 * @return the string value
	 */
	String getStringValue(F value);
	
	/**
	 * Gets the string value.
	 *
	 * @param value the value
	 * @return the string value
	 */
	String getStringValue(String value);
}
