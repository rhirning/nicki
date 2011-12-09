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

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.vaadin.base.application.NickiApplication;

import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class TableEditor extends NickiTreeEditor {
	private TableSelector tableSelector = new TableSelector();

	Table component = new Table();
	public TableEditor(NickiApplication application, NickiContext ctx, DataProvider treeDataProvider, String messageKeyBase) {
		super(application, ctx);
		init(tableSelector, treeDataProvider, messageKeyBase);
	}
}
