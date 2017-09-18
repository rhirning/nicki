
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

public enum Component {
	DATE("org.mgnl.nicki.shop.attributes.DateComponent"),
	TEXT("org.mgnl.nicki.shop.attributes.TextComponent"),
	CHECKBOX("org.mgnl.nicki.shop.attributes.CheckboxComponent"),
	SELECT("org.mgnl.nicki.shop.attributes.SelectComponent"),
	FREESELECT("org.mgnl.nicki.shop.attributes.FreeSelectComponent"),
	STATIC("org.mgnl.nicki.shop.attributes.LabelComponent"),
	GENERIC("org.mgnl.nicki.shop.attributes.GenericComponent"),
	DEFAULT("org.mgnl.nicki.shop.attributes.LabelComponent");

	private String className;

	Component(String className) {
		this.className = className;
	}

	public Object getInstance() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		return Classes.newInstance(className);
	}
}
