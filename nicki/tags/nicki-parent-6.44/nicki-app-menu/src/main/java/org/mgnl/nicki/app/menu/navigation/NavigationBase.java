package org.mgnl.nicki.app.menu.navigation;

import java.util.ArrayList;
import java.util.List;

import org.mgnl.nicki.app.menu.application.MainView;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.CustomComponent;

public abstract class NavigationBase extends CustomComponent implements Navigation {
	private static final long serialVersionUID = -4231539383235849692L;
	private MainView mainView;
	private List<NavigationElement> list = new ArrayList<NavigationElement>();
	
	public NavigationBase(MainView mainView) {
		this.mainView = mainView;
	}

	@Override
	public boolean select(NavigationEntry entry) {
		return mainView.show(entry);
	}
	
	public void add(NavigationElement navigationElement) {
		list.add(navigationElement);
	}
	
	@Override
	public Container getContainer() {
		return new BeanItemContainer<NavigationElement>(NavigationElement.class, list);
	}
	
	public void initContainer() {
		list = new ArrayList<NavigationElement>();
	}
}
