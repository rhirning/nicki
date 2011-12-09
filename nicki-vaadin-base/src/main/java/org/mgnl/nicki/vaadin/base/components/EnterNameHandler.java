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

public abstract class EnterNameHandler {
	
	private EnterNameDialog dialog;
	
	public abstract void setName(String name) throws Exception;
	
	
	public void setDialog(EnterNameDialog enterNameDialog) {
		this.dialog = enterNameDialog;
	}
	public EnterNameDialog getDialog() {
		return dialog;
	}

}
