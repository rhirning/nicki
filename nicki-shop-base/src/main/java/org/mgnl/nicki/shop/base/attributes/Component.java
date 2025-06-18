
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


import org.mgnl.nicki.core.util.Classes;


/**
 * The Enum Component.
 */
public enum Component {
	
	/** The date. */
	DATE("org.mgnl.nicki.shop.attributes.DateComponent"),
	
	/** The text. */
	TEXT("org.mgnl.nicki.shop.attributes.TextComponent"),
	
	/** The checkbox. */
	CHECKBOX("org.mgnl.nicki.shop.attributes.CheckboxComponent"),
	
	/** The select. */
	SELECT("org.mgnl.nicki.shop.attributes.SelectComponent"),
	
	/** The freeselect. */
	FREESELECT("org.mgnl.nicki.shop.attributes.FreeSelectComponent"),
	
	/** The static. */
	STATIC("org.mgnl.nicki.shop.attributes.LabelComponent"),
	
	/** The generic. */
	GENERIC("org.mgnl.nicki.shop.attributes.GenericComponent"),
	
	/** The default. */
	DEFAULT("org.mgnl.nicki.shop.attributes.LabelComponent");

	/** The class name. */
	private String className;

	/**
	 * Instantiates a new component.
	 *
	 * @param className the class name
	 */
	Component(String className) {
		this.className = className;
	}

	/**
	 * Gets the single instance of Component.
	 *
	 * @return single instance of Component
	 * @throws ClassNotFoundException the class not found exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	public Object getInstance() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		return Classes.newInstance(className);
	}
}
