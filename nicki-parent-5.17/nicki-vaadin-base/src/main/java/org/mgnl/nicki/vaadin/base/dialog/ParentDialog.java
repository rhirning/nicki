package org.mgnl.nicki.vaadin.base.dialog;

import org.mgnl.nicki.vaadin.base.navigation.NavigationCommand;

public interface ParentDialog {

	void initChildren();

	void navigate(NavigationCommand command);
}
