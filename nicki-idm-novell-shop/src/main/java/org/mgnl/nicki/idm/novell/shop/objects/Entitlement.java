
package org.mgnl.nicki.idm.novell.shop.objects;

/*-
 * #%L
 * nicki-idm-novell-shop
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


import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;

// TODO: Auto-generated Javadoc
/**
 * The Class Entitlement.
 */
@SuppressWarnings("serial")
@DynamicObject
@ObjectClass("DirXML-Entitlement")
public class Entitlement extends DynamicStructObject {
	
	/** The name. */
	@DynamicAttribute(externalName="cn", naming=true)
	private String name;
	
	/**
	 * Gets the source.
	 *
	 * @return the source
	 */
	public String getSource() {
		return getInfo("/ref/src");
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return getInfo("/ref/id");
	}
	
	/**
	 * Gets the parameter.
	 *
	 * @return the parameter
	 */
	public String getParameter() {
		return getInfo("/ref/param");
	}


}
