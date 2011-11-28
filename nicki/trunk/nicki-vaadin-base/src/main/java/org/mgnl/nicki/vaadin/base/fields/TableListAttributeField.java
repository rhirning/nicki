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
import java.util.Iterator;
import java.util.List;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.components.EnterNameDialog;
import org.mgnl.nicki.vaadin.base.components.EnterNameHandler;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectValueChangeListener;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class TableListAttributeField extends BaseDynamicAttributeField implements DynamicAttributeField, Serializable {

	private DynamicObject dynamicObject;
	private String attributeName;
	private AbsoluteLayout mainLayout;
	private Table entries;
	private Button newEntryButton;
	private Button deleteEntryButton;
	
	public void init(String attributeName, DynamicObject dynamicObject, DynamicObjectValueChangeListener objectListener) {

		this.dynamicObject = dynamicObject;
		this.attributeName = attributeName;
		
		buildMainLayout();
		entries.setCaption(getName(dynamicObject, attributeName));
		entries.setSelectable(true);
		entries.addContainerProperty(attributeName, String.class, null);

		
		@SuppressWarnings("unchecked")
		List<Object> values = (List<Object>) dynamicObject.get(attributeName);
		for (Iterator<Object> iterator = values.iterator(); iterator.hasNext();) {
			String value = (String) iterator.next();
			addItem(value);
		}
		newEntryButton.addListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				addEntry(entries);
			}
		});
		deleteEntryButton.addListener(new Button.ClickListener() {
			@Override
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
		EnterNameDialog dialog = new EnterNameDialog("nicki.editor.catalogs.entry.new");
		dialog.setHandler(new NameHandler());
		Window newWindow = new Window(
				I18n.getText("nicki.editor.catalogs.entry.new.window.title"), dialog);
		newWindow.setWidth(440, Sizeable.UNITS_PIXELS);
		newWindow.setHeight(500, Sizeable.UNITS_PIXELS);
		newWindow.setModal(true);
		mainLayout.getWindow().addWindow(newWindow);
	}

	private class NameHandler extends EnterNameHandler {
		
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

	
	@Override
	public ComponentContainer getComponent(boolean readOnly) {
		mainLayout.setReadOnly(readOnly);
		return mainLayout;
	}
	
	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		mainLayout.setWidth("600px");
		mainLayout.setHeight("160px");
		
		// entries
		entries = new Table();
		entries.setWidth("400px");
		entries.setHeight("120px");
		entries.setImmediate(true);
		mainLayout.addComponent(entries, "top:20.0px;left:0.0px;");
		
		// newEntryButton
		newEntryButton = new Button();
		newEntryButton.setWidth("-1px");
		newEntryButton.setHeight("-1px");
		newEntryButton.setCaption("Neu");
		newEntryButton.setImmediate(false);
		mainLayout.addComponent(newEntryButton, "top:20.0px;left:420.0px;");
		
		// deleteEntryButton
		deleteEntryButton = new Button();
		deleteEntryButton.setWidth("-1px");
		deleteEntryButton.setHeight("-1px");
		deleteEntryButton.setCaption("L�schen");
		deleteEntryButton.setImmediate(false);
		mainLayout.addComponent(deleteEntryButton,
				"top:60.0px;left:420.0px;");
		
		return mainLayout;
	}
}
