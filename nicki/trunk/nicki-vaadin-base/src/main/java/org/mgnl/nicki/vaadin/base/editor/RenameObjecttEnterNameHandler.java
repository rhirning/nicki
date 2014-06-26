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

import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.components.EnterNameHandler;

import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class RenameObjecttEnterNameHandler extends EnterNameHandler implements Serializable {
	private NickiTreeEditor editor;
	private DynamicObject dynamicObject;

	public RenameObjecttEnterNameHandler(NickiTreeEditor nickiEditor, DynamicObject dynamicObject) {
		super();
		this.editor = nickiEditor;
		this.dynamicObject = dynamicObject;
	}

	public void closeEnterNameDialog() {
		UI.getCurrent().removeWindow(getDialog());
	}

	public void setName(String name) throws DynamicObjectException {
		DynamicObject parent = editor.getParent(dynamicObject);
		dynamicObject.rename(name);
		editor.reloadChildren(parent);
	}

	@Override
	public String getName() {
		return dynamicObject.getName();
	}

}
