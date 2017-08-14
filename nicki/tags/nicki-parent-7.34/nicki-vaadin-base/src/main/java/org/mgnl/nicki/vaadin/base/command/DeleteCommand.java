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
package org.mgnl.nicki.vaadin.base.command;

import org.mgnl.nicki.core.data.InvalidActionException;
import org.mgnl.nicki.core.data.TreeData;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.vaadin.base.editor.NickiTreeEditor;

import com.vaadin.ui.Notification;

public class DeleteCommand implements Command {
	private TreeData target;
	private NickiTreeEditor nickiEditor;

	public DeleteCommand(NickiTreeEditor nickiEditor, TreeData target) {
		this.nickiEditor = nickiEditor;
		this.target = target;
	}

	public void execute() throws CommandException {
		TreeData parent = this.nickiEditor.getParent(this.target);
		try {
			this.target.delete();
		} catch (DynamicObjectException | InvalidActionException e) {
			throw new CommandException(e);
		}
		nickiEditor.getSelector().removeItem(target);
		Notification.show(I18n.getText("nicki.editor.delete.info"));
		if (parent != null) {
			this.nickiEditor.refresh(parent);
		}
	}

	public String getTitle() {
		return I18n.getText("nicki.editor.delete.title");
	}

	public String getHeadline() {
		return I18n.getText("nicki.editor.delete.headline", target.getDisplayName());
	}

	public String getCancelCaption() {
		return I18n.getText("nicki.editor.delete.cancel");
	}

	public String getConfirmCaption() {
		return I18n.getText("nicki.editor.delete.confirm");
	}

	public String getErrorText() {
		return I18n.getText("nicki.editor.delete.error");
	}
}
