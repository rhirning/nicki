package org.mgnl.nicki.vaadin.base.components;

import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.editor.NewObjectEnterNameHandler;
import org.mgnl.nicki.vaadin.base.editor.NickiEditor;


@SuppressWarnings("serial")
public class SimpleNewClassEditor extends EnterNameDialog implements NewClassEditor {
	private NickiEditor treeEditor;

	public SimpleNewClassEditor(NickiEditor nickiEditor, String messageBase) {
		super(messageBase);
		this.treeEditor = nickiEditor;
	}

	@Override
	public void init(DynamicObject parent, Class<?> classDefinition) {
		setHandler( new NewObjectEnterNameHandler(treeEditor, parent, classDefinition));
	}
}
