
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

import org.jdom.Element;
import org.mgnl.nicki.core.i18n.I18n;

@SuppressWarnings("serial")
public class CatalogArticleAttribute implements Serializable{
	public static final String SEPARATOR = "|"; 
	private String name;
	private String key;
	private String type;
	private String contentClass;

	public CatalogArticleAttribute(Element attributeElement) {
		this.name = attributeElement.getAttributeValue("name");
		this.key = attributeElement.getAttributeValue("label");
		this.type = attributeElement.getAttributeValue("type");
		this.contentClass = attributeElement.getAttributeValue("contentClass");
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLabel() {
		return I18n.getText(key);
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getAttributeString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name).append(SEPARATOR).append(key).append(SEPARATOR).append(type);
		return sb.toString();
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getKey() {
		return key;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[catalogArticleAttribute name='").append(getName());
		sb.append("' label='").append(getLabel());
		sb.append("' key='").append(getKey());
		sb.append("' type='").append(getType());
		sb.append("']\n");
		return sb.toString();
	}

	public String getContentClass() {
		return contentClass;
	}
}
