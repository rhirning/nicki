package org.mgnl.nicki.vaadin.base.dialog;

import com.vaadin.ui.Component;

public interface ControlledDialog extends Component {
	boolean validate();
	void save();
	boolean hasChanges();
}
