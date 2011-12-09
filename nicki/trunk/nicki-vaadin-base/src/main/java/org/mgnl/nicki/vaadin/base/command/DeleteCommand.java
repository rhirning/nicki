/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.command;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.vaadin.base.editor.NickiTreeEditor;

public class DeleteCommand implements Command {
	private DynamicObject target;
	private NickiTreeEditor nickiEditor;

	public DeleteCommand(NickiTreeEditor nickiEditor, DynamicObject target) {
		this.nickiEditor = nickiEditor;
		this.target = target;
	}

	public void execute() throws CommandException {
		DynamicObject parent = this.nickiEditor.getParent(this.target);
		try {
			this.target.delete();
		} catch (DynamicObjectException e) {
			throw new CommandException(e);
		}
		nickiEditor.getSelector().removeItem(target);
		this.nickiEditor.getWindow().showNotification(I18n.getText("nicki.editor.delete.info"));
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
