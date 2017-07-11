package org.mgnl.nicki.vaadin.base.dialog;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.CustomComponent;

public abstract class NavigationBase extends CustomComponent implements Navigation {
	private static final long serialVersionUID = -4231539383235849692L;
	private NavigationSelector selector;
	private List<NavigationElement> list = new ArrayList<NavigationElement>();
	
	public NavigationBase(NavigationSelector mainView) {
		this.selector = mainView;
	}

	@Override
	public boolean select(NavigationEntry entry) {
		return selector.show(entry);
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
