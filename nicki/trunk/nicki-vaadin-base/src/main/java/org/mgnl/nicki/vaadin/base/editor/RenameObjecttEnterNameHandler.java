package org.mgnl.nicki.vaadin.base.editor;

import java.io.Serializable;

import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.vaadin.base.components.EnterNameHandler;

@SuppressWarnings("serial")
public class RenameObjecttEnterNameHandler extends EnterNameHandler implements Serializable {
	private NickiEditor editor;
	private DynamicObject dynamicObject;

	public RenameObjecttEnterNameHandler(NickiEditor nickiEditor, DynamicObject dynamicObject) {
		super();
		this.editor = nickiEditor;
		this.dynamicObject = dynamicObject;
	}

	public void closeEnterNameDialog() {
		editor.getWindow().removeWindow(getDialog().getWindow());
	}

	public void setName(String name) throws DynamicObjectException {
		DynamicObject parent = editor.getParent(dynamicObject);
		dynamicObject.rename(name);
		editor.reloadChildren(parent);
	}

}
