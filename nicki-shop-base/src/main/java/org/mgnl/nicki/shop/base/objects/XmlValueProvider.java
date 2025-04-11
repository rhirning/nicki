
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
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.helper.XMLHelper;

import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * XML Format:
 * 
 * <data onlyDefinedEntries="true">
 *   <entry value="A" display="Aha"/>
 * </data>.
 *
 * @author rhirning
 */

@Slf4j
public class XmlValueProvider implements CatalogValueProvider {

	/** The entries. */
	private Map<String, String> entries = new TreeMap<String, String>();
	
	/** The only defined entries. */
	private boolean onlyDefinedEntries = false;
	
	/**
	 * Checks if is only defined entries.
	 *
	 * @return true, if is only defined entries
	 */
	@Override
	public boolean isOnlyDefinedEntries() {
		return onlyDefinedEntries;
	}

	/**
	 * Gets the entries.
	 *
	 * @return the entries
	 */
	@Override
	public Map<String, String> getEntries() {
		return entries;
	}

	/**
	 * Check entry.
	 *
	 * @param entry the entry
	 * @return true, if successful
	 */
	@Override
	public boolean checkEntry(String entry) {
		if (onlyDefinedEntries) {
			return entries.containsKey(entry);
		} else {
			return true;
		}
	}

	
	/**
	 * Inits the.
	 *
	 * @param article the article
	 */
	public void init(CatalogArticle article) {
		String providerData = article.getAttribute("providerData");
		if (StringUtils.isNotEmpty(providerData)) {
			try {
				Document document = XMLHelper.documentFromString(providerData);
				Element docRoot = document.getRootElement();
				this.onlyDefinedEntries = true;
				String onlyDefinedEntries = docRoot.getAttributeValue("onlyDefinedEntries");
				if (StringUtils.isNotEmpty(onlyDefinedEntries)) {
					this.onlyDefinedEntries = DataHelper.booleanOf(onlyDefinedEntries);
				}
				List<Element> entryList = document.getRootElement().getChildren("entry");
				if (entryList != null) {
					for (Element element : entryList) {
						entries.put(element.getAttributeValue("value"), element.getAttributeValue("display"));
					}
				}
			} catch (Exception e) {
				log.error("Error", e);
			}
		}
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	@Override
	public TYPE getType() {
		return TYPE.LIST;
	}
	

}
