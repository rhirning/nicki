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


import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.data.InvalidActionException;
import org.mgnl.nicki.core.data.TreeData;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.vaadin.base.components.NewClassEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class DynamicObjectViewer extends CustomComponent implements NewClassEditor, ClassEditor {
	private static final Logger LOG = LoggerFactory.getLogger(DynamicObjectViewer.class);

	private VerticalLayout mainLayout;
	private DynamicObject dynamicObject;
	private Button saveButton;
	private boolean create;
	private DynamicObjectValueChangeListener<String> listener;
	private DynamicObject parent;

	@Deprecated
	public DynamicObjectViewer(DynamicObject dynamicObject) {
		this.dynamicObject = dynamicObject;
		this.create = false;
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	public DynamicObjectViewer() {
	}

	public void setDynamicObject(NickiTreeEditor nickiEditor, TreeData dynamicObject) {
		LOG.debug("DynamicObject: " + dynamicObject);
		this.dynamicObject = (DynamicObject) dynamicObject;
		this.create = false;
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}
	
	public DynamicObjectViewer(DynamicObjectValueChangeListener<String> listener) {
		this.listener = listener;
	}
	
	public void init(TreeData parent, Class<? extends TreeData> classDefinition) throws InstantiateDynamicObjectException, DynamicObjectException {
		this.parent = (DynamicObject) parent;
		try {
			this.dynamicObject = (DynamicObject) this.parent.createChild(classDefinition, "");
		} catch (InvalidActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.create = true;
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}


	private VerticalLayout buildMainLayout() {
		
		mainLayout = new VerticalLayout();
		mainLayout.setMargin(true);
		mainLayout.setWidth("100%");
		Label label = new Label(dynamicObject.getClass().getName());
		mainLayout.addComponent(label);
		DynamicObjectFieldFactory factory = new DynamicObjectFieldFactory(listener);
		factory.addFields(mainLayout, dynamicObject, create);
		
		saveButton = new Button(I18n.getText("nicki.editor.generic.button.save"));
		saveButton.addClickListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				save();
			}
		});

		mainLayout.addComponent(saveButton);
		return mainLayout;
	}

	public void save() {
		try {
			if (create) {
				dynamicObject.create();
			} else {
				Notification.show(I18n.getText("nicki.editor.save.info"));
				dynamicObject.update();
			}
			if (listener != null) {
				listener.close(this);
				listener.refresh(this.parent);
			}
		} catch (Exception e) {
			LOG.error("Error", e);
		}
	}

	public boolean isCreate() {
		return create;
	}
}
