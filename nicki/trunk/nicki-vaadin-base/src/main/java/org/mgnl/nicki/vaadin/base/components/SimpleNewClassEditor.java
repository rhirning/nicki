/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.components;

import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.editor.NewObjectEnterNameHandler;
import org.mgnl.nicki.vaadin.base.editor.NickiTreeEditor;


@SuppressWarnings("serial")
public class SimpleNewClassEditor extends EnterNameDialog implements NewClassEditor {
	private NickiTreeEditor treeEditor;

	public SimpleNewClassEditor(NickiTreeEditor nickiEditor, String messageBase) {
		super(messageBase);
		this.treeEditor = nickiEditor;
	}

	public void init(DynamicObject parent, Class<? extends DynamicObject> classDefinition) {
		setHandler( new NewObjectEnterNameHandler(treeEditor, parent, classDefinition));
	}
}
