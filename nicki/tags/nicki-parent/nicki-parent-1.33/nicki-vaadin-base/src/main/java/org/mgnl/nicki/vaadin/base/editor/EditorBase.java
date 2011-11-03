package org.mgnl.nicki.vaadin.base.editor;

import java.io.Serializable;

import org.mgnl.nicki.ldap.context.NickiContext;

import com.vaadin.ui.CustomComponent;

@SuppressWarnings("serial")
public abstract class EditorBase extends CustomComponent implements Serializable {
	public EditorBase(NickiContext context) {
		super();
		this.context = context;
	}

	private NickiContext context;

	public void setContext(NickiContext context) {
		this.context = context;
	}

	public NickiContext getContext() {
		return context;
	}


}
