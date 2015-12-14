package org.mgnl.nicki.vaadin.base.dialog;

public interface DialogController {
	void closeDialog(ControlledDialog dialog);
	void saveDialog(ControlledDialog dialog);
	void validateDialog(ControlledDialog dialog);

}
