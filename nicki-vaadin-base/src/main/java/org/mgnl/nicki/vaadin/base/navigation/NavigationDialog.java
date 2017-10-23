package org.mgnl.nicki.vaadin.base.navigation;

import com.vaadin.ui.Component;

public interface NavigationDialog extends Component {
	void navigate(NavigationCommand command);
}
