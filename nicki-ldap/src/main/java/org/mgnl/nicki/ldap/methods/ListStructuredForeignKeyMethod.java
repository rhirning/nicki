/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.ldap.methods;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicObject;

@SuppressWarnings("serial")
public class ListStructuredForeignKeyMethod extends ListForeignKeyMethod {

	public ListStructuredForeignKeyMethod(NickiContext context,
			ContextSearchResult rs, String ldapName,
			Class<? extends DynamicObject> classDefinition) {
		super(context, rs, ldapName, classDefinition);
	}

	public List<DynamicObject> exec(@SuppressWarnings("rawtypes") List arguments) {
		if (getObjects() == null) {
			setObjects(new ArrayList<DynamicObject>());
			for (Iterator<Object> iterator = this.getForeignKeys().iterator(); iterator.hasNext();) {
				String structuredForeignKey = (String) iterator.next();
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
					System.out.println("Could not build object: " + path);
				}
			}
		}
		return getObjects();
	}

}
