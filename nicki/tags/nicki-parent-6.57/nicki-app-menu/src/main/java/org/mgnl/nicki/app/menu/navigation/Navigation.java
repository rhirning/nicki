package org.mgnl.nicki.app.menu.navigation;

import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.ui.Component;

public interface Navigation extends Component {

	boolean select(NavigationEntry entry);
	
	void init(List<NavigationFolder> navigationFolders);
	
	Container getContainer();

	void selectInNavigation(NavigationEntry entry);

}
