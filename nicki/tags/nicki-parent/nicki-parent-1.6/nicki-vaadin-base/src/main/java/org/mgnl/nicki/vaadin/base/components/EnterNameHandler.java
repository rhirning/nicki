package org.mgnl.nicki.vaadin.base.components;

public abstract class EnterNameHandler {
	
	private EnterNameDialog dialog;
	
	public abstract boolean setName(String name);
	
	
	public void setDialog(EnterNameDialog enterNameDialog) {
		this.dialog = enterNameDialog;
	}
	public EnterNameDialog getDialog() {
		return dialog;
	}

}
