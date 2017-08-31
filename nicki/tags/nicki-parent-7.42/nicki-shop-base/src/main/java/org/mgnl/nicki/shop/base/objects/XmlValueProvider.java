/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.shop.base.objects;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.helper.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XML Format:
 * 
 * <data onlyDefinedEntries="true">
 *   <entry value="A" display="Aha"/>
 * </data>
 * @author rhirning
 *
 */


public class XmlValueProvider implements CatalogValueProvider {
	private static final Logger LOG = LoggerFactory.getLogger(XmlValueProvider.class);

	private Map<String, String> entries = new TreeMap<String, String>();
	private boolean onlyDefinedEntries = false;
	
	@Override
	public boolean isOnlyDefinedEntries() {
		return onlyDefinedEntries;
	}

	@Override
	public Map<String, String> getEntries() {
		return entries;
	}

	@Override
	public boolean checkEntry(String entry) {
		if (onlyDefinedEntries) {
			return entries.containsKey(entry);
		} else {
			return true;
		}
	}

	
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
				@SuppressWarnings("unchecked")
				List<Element> entryList = document.getRootElement().getChildren("entry");
				if (entryList != null) {
					for (Element element : entryList) {
						entries.put(element.getAttributeValue("value"), element.getAttributeValue("display"));
					}
				}
			} catch (Exception e) {
				LOG.error("Error", e);
			}
		}
	}

	@Override
	public TYPE getType() {
		return TYPE.LIST;
	}
	

}
