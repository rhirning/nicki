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
package org.mgnl.nicki.vaadin.base.editor;

import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.DataProvider;
import org.mgnl.nicki.vaadin.base.application.NickiApplication;

import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class TableEditor extends NickiTreeEditor {
	private TableSelector tableSelector = new TableSelector();

	Table component = new Table();
	public TableEditor(NickiApplication application, NickiContext ctx, DataProvider treeDataProvider, String messageKeyBase) {
		super(application, ctx);
		init(tableSelector, treeDataProvider, messageKeyBase);
	}
}
