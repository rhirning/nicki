package org.mgnl.nicki.vaadin.base.editor;

import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.vaadin.base.application.NickiApplication;

@SuppressWarnings("serial")
public class TreeEditor extends NickiTreeEditor {
	private TreeSelector treeSelector = new TreeSelector();
	public TreeEditor(NickiApplication application, NickiContext ctx, DataProvider treeDataProvider, String messageKeyBase) {
		super(application, ctx);
		init(treeSelector, treeDataProvider, messageKeyBase);
	}
}
