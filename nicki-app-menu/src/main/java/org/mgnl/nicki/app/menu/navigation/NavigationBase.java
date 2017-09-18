
package org.mgnl.nicki.app.menu.navigation;

/*-
 * #%L
 * nicki-app-menu
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


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
