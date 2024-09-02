
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


import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.methods.StructuredData;
import org.mgnl.nicki.core.objects.BaseDynamicObject;


@SuppressWarnings("serial")
public abstract class DynamicStructObject extends BaseDynamicObject {

	public static final String SEPARATOR = "/";
	/*
	 * Beispiel /ref/src
	 */
	public String getInfo(String infoPath) {
		String parts[] = StringUtils.split(infoPath, SEPARATOR);
		if (parts.length < 2) {
			return null;
		}
		try {
			Element element = getDocument().getRootElement();
			int i = 1;
			while (i < parts.length - 1) {
				element = element.getChild(parts[i]);
				i++;
			}
			
			return element.getChildTextTrim(parts[i]);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Date getDateInfo(String infoPath) {
		try {
			String info = StringUtils.removeEnd(getInfo(infoPath), "Z");
			return DataHelper.timeFromString(info);
		} catch (Exception e) {
			return null;
		}
	}

	public String getFlag() {
		return getAttribute("struct:flag");
	}

	public String getXml() {
		return getAttribute("struct:xml");
	}
	
	public Document getDocument() {
		return (Document) ((StructuredData) get("struct")).getDocument();
	}
	
}
