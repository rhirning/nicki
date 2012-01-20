/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
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
