package org.mgnl.nicki.vaadin.base.editor;

import java.io.Serializable;

import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.components.EnterNameHandler;

@SuppressWarnings("serial")
public class NewObjectEnterNameHandler extends EnterNameHandler implements Serializable {
	private NickiEditor editor;
	private DynamicObject parent;
	private Class<?> classDefinition;
	

	public NewObjectEnterNameHandler(NickiEditor treeEditor, DynamicObject parent,
			Class<?> classDefinition) {
		super();
		this.editor = treeEditor;
		this.parent = parent;
		this.classDefinition = classDefinition;
	}

	public void setName(String name) throws Exception {
		if (editor.create(parent, classDefinition, name)) {
			editor.refresh(parent);
		}
	}

}
