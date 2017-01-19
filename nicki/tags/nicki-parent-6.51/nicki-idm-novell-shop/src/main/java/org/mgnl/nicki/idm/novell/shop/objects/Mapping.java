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
