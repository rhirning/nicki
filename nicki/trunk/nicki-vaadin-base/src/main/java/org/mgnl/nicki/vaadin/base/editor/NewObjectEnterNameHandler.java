/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.editor;

import java.io.Serializable;

import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.components.EnterNameHandler;

@SuppressWarnings("serial")
public class NewObjectEnterNameHandler extends EnterNameHandler implements Serializable {
	private NickiTreeEditor editor;
	private DynamicObject parent;
	private Class<? extends DynamicObject> classDefinition;
	

	public NewObjectEnterNameHandler(NickiTreeEditor treeEditor, DynamicObject parent,
			Class<? extends DynamicObject> classDefinition) {
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
