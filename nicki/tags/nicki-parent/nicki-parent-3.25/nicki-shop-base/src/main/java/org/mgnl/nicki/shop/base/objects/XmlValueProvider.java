/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
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
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
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
	

}
