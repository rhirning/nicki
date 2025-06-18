
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


import java.io.Serializable;

import org.jdom2.Element;
import org.mgnl.nicki.core.i18n.I18n;


/**
 * The Class CatalogArticleAttribute.
 */
@SuppressWarnings("serial")
public class CatalogArticleAttribute implements Serializable{
	
	/** The Constant SEPARATOR. */
	public static final String SEPARATOR = "|"; 
	
	/** The name. */
	private String name;
	
	/** The key. */
	private String key;
	
	/** The type. */
	private String type;
	
	/** The content class. */
	private String contentClass;

	/**
	 * Instantiates a new catalog article attribute.
	 *
	 * @param attributeElement the attribute element
	 */
	public CatalogArticleAttribute(Element attributeElement) {
		this.name = attributeElement.getAttributeValue("name");
		this.key = attributeElement.getAttributeValue("label");
		this.type = attributeElement.getAttributeValue("type");
		this.contentClass = attributeElement.getAttributeValue("contentClass");
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel() {
		return I18n.getText(key);
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Gets the attribute string.
	 *
	 * @return the attribute string
	 */
	public String getAttributeString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name).append(SEPARATOR).append(key).append(SEPARATOR).append(type);
		return sb.toString();
	}
	
	/**
	 * Sets the key.
	 *
	 * @param key the new key
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[catalogArticleAttribute name='").append(getName());
		sb.append("' label='").append(getLabel());
		sb.append("' key='").append(getKey());
		sb.append("' type='").append(getType());
		sb.append("']\n");
		return sb.toString();
	}

	/**
	 * Gets the content class.
	 *
	 * @return the content class
	 */
	public String getContentClass() {
		return contentClass;
	}
}
