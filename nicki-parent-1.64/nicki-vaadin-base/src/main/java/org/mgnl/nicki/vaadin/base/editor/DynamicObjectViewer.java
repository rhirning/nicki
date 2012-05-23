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
package org.mgnl.nicki.vaadin.base.editor;


import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.vaadin.base.components.NewClassEditor;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class DynamicObjectViewer extends CustomComponent implements NewClassEditor, ClassEditor {

	private AbsoluteLayout mainLayout;
	private DynamicObject dynamicObject;
	private Button saveButton;
	private boolean create;
	private DynamicObjectValueChangeListener listener = null;
	private DynamicObject parent = null;

	@Deprecated
	public DynamicObjectViewer(DynamicObject dynamicObject) {
		this.dynamicObject = dynamicObject;
		this.create = false;
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	public DynamicObjectViewer() {
	}

	public void setDynamicObject(NickiTreeEditor nickiEditor, DynamicObject dynamicObject) {
		this.dynamicObject = dynamicObject;
		this.create = false;
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}
	
	public DynamicObjectViewer(DynamicObjectValueChangeListener listener) {
		this.listener = listener;
	}
	
	public void init(DynamicObject parent, Class<? extends DynamicObject> classDefinition) throws InstantiateDynamicObjectException, DynamicObjectException {
		this.parent = parent;
		this.dynamicObject = parent.getContext().getObjectFactory().getNewDynamicObject(classDefinition, parent.getPath(), "");
		this.create = true;
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}


	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		VerticalLayout layout = new VerticalLayout();
		mainLayout.addComponent(layout, "top:20.0px;left:20.0px;");
		
		layout.addComponent(getLayout());
		
		saveButton = new Button(I18n.getText("nicki.editor.generic.button.save"));
		saveButton.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				save();
			}
		});

		layout.addComponent(saveButton);
		return mainLayout;
	}
	
	protected Layout getLayout() {
		VerticalLayout layout = new VerticalLayout();
		DynamicObjectFieldFactory factory = new DynamicObjectFieldFactory(listener);
		factory.addFields(layout, dynamicObject, create);
		return layout;
	}

	protected void save() {
		try {
			if (create) {
				dynamicObject.create();
			} else {
				dynamicObject.update();
			}
			if (listener != null) {
				listener.close(this);
				listener.refresh(this.parent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isCreate() {
		return create;
	}
}
