package org.mgnl.nicki.vaadin.base.command;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.vaadin.base.editor.NickiEditor;

public class DeleteCommand implements Command {
	private DynamicObject target;
	private NickiEditor nickiEditor;

	public DeleteCommand(NickiEditor nickiEditor, DynamicObject target) {
		this.nickiEditor = nickiEditor;
		this.target = target;
	}

	@Override
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

	@Override
	public Object getHeadline() {
		return I18n.getText("nicki.editor.delete.headline", target.getName());
	}

	@Override
	public String getCancelCaption() {
		return I18n.getText("nicki.editor.delete.cancel");
	}

	@Override
	public String getConfirmCaption() {
		return I18n.getText("nicki.editor.delete.confirm");
	}

	@Override
	public String getErrorText() {
		return I18n.getText("nicki.editor.delete.error");
	}
}
