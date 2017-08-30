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
package org.mgnl.nicki.idm.novell.shop.objects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.annotation.StructuredDynamicAttribute;
import org.mgnl.nicki.core.helper.NameValue;
import org.mgnl.nicki.core.helper.XMLHelper;
import org.mgnl.nicki.core.objects.BaseDynamicObject;

@DynamicObject
@ObjectClass("DirXML-Resource")
public class Mapping extends BaseDynamicObject {

	private static final long serialVersionUID = -6344126171035952327L;
	@DynamicAttribute(externalName="cn", naming=true)
	private String name;
	@StructuredDynamicAttribute(externalName="DirXML-Data")
	private String data;
	@DynamicAttribute(externalName="DirXML-ContentType")
	private String contentType;
	
	
	@SuppressWarnings("unchecked")
	public List<NameValue> getMapping() throws JDOMException, IOException   {
		List<NameValue> list = new ArrayList<NameValue>();

		Document doc = XMLHelper.documentFromString(getAttribute("data"));
		
		Element root = doc.getRootElement();
		
		List<Element> rows = root.getChildren("row");
		if (rows != null &&  rows.size() > 0) {
			for (Element row : rows) {
				List<Element> cols = row.getChildren("col");
				if (cols != null &&  cols.size() == 2) {
					list.add(new NameValue(cols.get(0).getText(), cols.get(1).getText()));
				}
			}
		}

		return list;
	}

}
