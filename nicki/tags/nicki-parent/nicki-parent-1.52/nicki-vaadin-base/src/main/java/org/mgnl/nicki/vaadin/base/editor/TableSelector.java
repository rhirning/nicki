/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
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
 */
package org.mgnl.nicki.vaadin.base.editor;

import com.vaadin.event.Action.Handler;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class TableSelector extends BasicNickiSelector {
	private Table component = new Table();

	public TableSelector() {
		super();
		super.setComponent(component);
	}

	public void setSelectable(boolean selectable) {
		component.setSelectable(selectable);
	}

	public void addActionHandler(Handler handler) {
		component.addActionHandler(handler);
	}

}
