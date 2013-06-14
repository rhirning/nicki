package org.mgnl.nicki.vaadin.base.dialog;

public interface ControlledDialog {
	boolean validate();
	void save();
	boolean hasChanges();
}
