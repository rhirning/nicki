/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.editor.projects.core;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Project;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.editor.ClassEditor;
import org.mgnl.nicki.vaadin.base.editor.Icon;
import org.mgnl.nicki.vaadin.base.editor.NickiTreeEditor;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TabSheet;

@SuppressWarnings("serial")
public class ProjectViewer extends CustomComponent implements ClassEditor {

	@AutoGenerated
	private AbsoluteLayout mainLayout;
	@AutoGenerated
	private TabSheet tabSheet;
	private Project project;

	private ClassEditor dataView = new ProjectDataView();
	private ClassEditor mailView = new ProjectMailView();

	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public ProjectViewer() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
		// Project main data
		tabSheet.addTab(dataView, I18n.getText("nicki.editor.projects.tab.data.title"), Icon.USERS.getResource());
		tabSheet.addTab(mailView, I18n.getText("nicki.editor.projects.tab.mail.title"), Icon.EMAIL.getResource());
	}
	
	@Override
	public void setDynamicObject(NickiTreeEditor nickiEditor,
			DynamicObject dynamicObject) {
		this.project = (Project) dynamicObject;
		dataView.setDynamicObject(nickiEditor, this.project);
		mailView.setDynamicObject(nickiEditor, this.project);
	}


	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// tabSheet
		tabSheet = new TabSheet();
		tabSheet.setWidth("100.0%");
		tabSheet.setHeight("100.0%");
		tabSheet.setImmediate(false);
		mainLayout.addComponent(tabSheet,
				"top:20.0px;right:20.0px;bottom:20.0px;left:20.0px;");
		
		return mainLayout;
	}


}
