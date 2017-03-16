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
package org.mgnl.nicki.vaadin.base.fields;

import java.io.Serializable;
import java.util.List;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.components.EnterNameDialog;
import org.mgnl.nicki.vaadin.base.components.EnterNameHandler;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectValueChangeListener;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class TableListAttributeField extends BaseDynamicAttributeField implements DynamicAttributeField<String>, Serializable {

	private DynamicObject dynamicObject;
	private String attributeName;
	private HorizontalLayout mainLayout;
	private Table entries;
	private Button newEntryButton;
	private Button deleteEntryButton;
	
	public void init(String attributeName, DynamicObject dynamicObject, DynamicObjectValueChangeListener<String> objectListener) {

		this.dynamicObject = dynamicObject;
		this.attributeName = attributeName;
		
		buildMainLayout();
		//entries.setCaption(getName(dynamicObject, attributeName));
		entries.setSelectable(true);
		entries.addContainerProperty(attributeName, String.class, null);

		
		@SuppressWarnings("unchecked")
		List<Object> values = (List<Object>) dynamicObject.get(attributeName);
		for (Object valueObject : values) {
			String value = (String) valueObject;
			addItem(value);
		}
		newEntryButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				addEntry(entries);
			}
		});
		deleteEntryButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				deleteEntry(entries);
			}
		});
	}
	
	private void addItem(String value) {
		entries.addItem(new Object[] {value}, value);
	}

	protected void deleteEntry(Table table) {
		if (table.getValue() != null) {
			String valueToDelete = (String) table.getValue();
			table.removeItem(valueToDelete);
			@SuppressWarnings("unchecked")
			List<String> values = (List<String>) dynamicObject.get(attributeName);
			if (values.contains(valueToDelete)) {
				values.remove(valueToDelete);
				dynamicObject.put(attributeName, values);
			}
		}
	}
	protected void addEntry(Table table) {
		EnterNameDialog dialog = new EnterNameDialog("nicki.editor.catalogs.entry.new",
				I18n.getText("nicki.editor.catalogs.entry.new.window.title"));
		dialog.setHandler(new NameHandler(""));
		dialog.setWidth(440, Unit.PIXELS);
		dialog.setHeight(500, Unit.PIXELS);
		dialog.setModal(true);
		UI.getCurrent().addWindow(dialog);
	}

	private class NameHandler extends EnterNameHandler {
		
		public NameHandler(String initialName) {
			super(initialName);
		}

		@Override
		public void setName(String name) throws Exception {
			addItem(name);
			@SuppressWarnings("unchecked")
			List<String> values = (List<String>) dynamicObject.get(attributeName);
			if (!values.contains(name)) {
				values.add(name);
				dynamicObject.put(attributeName, values);
			}
		}
	}

	
	public ComponentContainer getComponent(boolean readOnly) {
		mainLayout.setReadOnly(readOnly);
		return mainLayout;
	}
	
	private HorizontalLayout buildMainLayout() {

		mainLayout = new HorizontalLayout();
		mainLayout.setHeight("160px");
		mainLayout.setSpacing(true);
		
		// top-level component properties
		mainLayout.setWidth("-1px");
		
		// entries
		entries = new Table();
		entries.setWidth("600px");
		entries.setHeight("100%");
		entries.setImmediate(true);
		mainLayout.addComponent(entries);
		
		VerticalLayout verticalLayout = new VerticalLayout();
		mainLayout.addComponent(verticalLayout);
		// newEntryButton
		newEntryButton = new Button();
		newEntryButton.setWidth("-1px");
		newEntryButton.setHeight("-1px");
		newEntryButton.setCaption("Neu");
		newEntryButton.setImmediate(false);
		verticalLayout.addComponent(newEntryButton);
		
		// deleteEntryButton
		deleteEntryButton = new Button();
		deleteEntryButton.setWidth("-1px");
		deleteEntryButton.setHeight("-1px");
		deleteEntryButton.setCaption("Löschen");
		deleteEntryButton.setImmediate(false);
		verticalLayout.addComponent(deleteEntryButton);
		return mainLayout;
	}
}
