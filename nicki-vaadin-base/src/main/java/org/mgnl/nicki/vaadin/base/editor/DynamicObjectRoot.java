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
package org.mgnl.nicki.vaadin.base.editor;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.ChildFilter;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.data.DataProvider;
import org.mgnl.nicki.core.data.EntryFilter;
import org.mgnl.nicki.core.data.TreeData;


@SuppressWarnings("serial")
public class DynamicObjectRoot implements DataProvider, Serializable {
	private String baseDn;
	private EntryFilter entryFilter;

	public DynamicObjectRoot(String baseDn, EntryFilter entryFilter) {
		super();
		this.baseDn = baseDn;
		this.entryFilter = entryFilter;
	}

	public List<? extends TreeData> getChildren(NickiContext context) {
		List<? extends DynamicObject> list = context.loadChildObjects(baseDn, new ChildFilter());
		Collections.sort(list, new Comparator<DynamicObject>() {

			@Override
			public int compare(DynamicObject o1, DynamicObject o2) {
				// TODO Auto-generated method stub
				return StringUtils.lowerCase(o1.getName()).compareTo(StringUtils.lowerCase(o2.getName()));
			}
		});
		return list;
	}

	public DynamicObject getRoot(NickiContext context) {
		return context.loadObject(baseDn);
	}

	public String getMessage() {
		return "";
	}

	public EntryFilter getEntryFilter() {
		return this.entryFilter;
	}

}
