/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
