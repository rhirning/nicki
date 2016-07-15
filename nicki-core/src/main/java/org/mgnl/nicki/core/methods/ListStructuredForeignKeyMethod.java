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
package org.mgnl.nicki.core.methods;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class ListStructuredForeignKeyMethod extends ListForeignKeyMethod {
	private static final Logger LOG = LoggerFactory.getLogger(ListStructuredForeignKeyMethod.class);
	public ListStructuredForeignKeyMethod(NickiContext context,
			ContextSearchResult rs, String ldapName,
			Class<? extends DynamicObject> classDefinition) {
		super(context, rs, ldapName, classDefinition);
	}

	public List<DynamicObject> exec(@SuppressWarnings("rawtypes") List arguments) {
		if (getObjects() == null) {
			setObjects(new ArrayList<DynamicObject>());
			for (Object key : this.getForeignKeys()) {
				String structuredForeignKey = (String) key;
				String path = StringUtils.substringBefore(structuredForeignKey, "#");
				String rest = StringUtils.substringAfter(structuredForeignKey, "#");
				String flag = StringUtils.substringBefore(rest, "#");
				String xml = StringUtils.substringAfter(rest, "#");

				DynamicObject object = getContext().loadObject(path);
				if (object != null) {
					object.put("struct:flag" , flag);
					object.put("struct:xml" , xml);
					object.put("struct" , new StructuredData(xml));
					getObjects().add(object);
				} else {
					LOG.debug("Could not build object: " + path);
				}
			}
		}
		return getObjects();
	}

}
