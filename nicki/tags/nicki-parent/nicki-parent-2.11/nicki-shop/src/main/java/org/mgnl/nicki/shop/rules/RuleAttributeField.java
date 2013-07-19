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
package org.mgnl.nicki.shop.rules;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.shop.objects.CatalogArticle;
import org.mgnl.nicki.shop.objects.Selector;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectValueChangeListener;
import org.mgnl.nicki.vaadin.base.fields.BaseDynamicAttributeField;
import org.mgnl.nicki.vaadin.base.fields.DynamicAttributeField;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RuleAttributeField extends BaseDynamicAttributeField implements DynamicAttributeField, Serializable {

	private CatalogArticle article;
	private String attributeName;
	private AbsoluteLayout mainLayout;
	private Table entries;
	private Button newEntryButton;
	private Button deleteEntryButton;
	private Button testButton;
	
	public void init(String attributeName, DynamicObject dynamicObject, DynamicObjectValueChangeListener objectListener) {

		this.article = (CatalogArticle) dynamicObject;
		this.attributeName = attributeName;
		
		buildMainLayout();
		entries.setCaption(getName(dynamicObject, attributeName));
		entries.setSelectable(true);
		entries.addContainerProperty(attributeName, String.class, null);

		
		@SuppressWarnings("unchecked")
		List<Object> values = (List<Object>) dynamicObject.get(attributeName);
		for (Object valueObject : values) {
			String value = (String) valueObject;
			addItem(value);
		}
		newEntryButton.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				addEntry(entries);
			}
		});
		deleteEntryButton.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				deleteEntry(entries);
			}
		});
		testButton.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				showTest();
			}
		});
	}
	
	protected void showTest() {
		
		Collection<Person> persons = RuleManager.getUsers(article, null);
		Table table = new Table();
		table.setWidth(440, Sizeable.UNITS_PIXELS);
		table.setHeight(500, Sizeable.UNITS_PIXELS);
		table.addContainerProperty(I18n.getText("nicki.editor.catalogs.rule.test.column.title"), String.class,  null);
		table.setVisibleColumns(new String[] { I18n.getText("nicki.editor.catalogs.rule.test.column.title") });
		table.setColumnHeaders(new String[] { I18n.getText("nicki.editor.catalogs.rule.test.column.title") });
		for (Person person : persons) {
			table.addItem(new String[] {person.getDisplayName()}, person.getDisplayName());
		}

		Window newWindow = new Window(I18n.getText("nicki.editor.catalogs.rule.test.window.title"));
        VerticalLayout layout = (VerticalLayout) newWindow.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);

		newWindow.addComponent(table);
		newWindow.setWidth(500, Sizeable.UNITS_PIXELS);
		newWindow.setHeight(620, Sizeable.UNITS_PIXELS);
		newWindow.setModal(true);
		mainLayout.getWindow().addWindow(newWindow);
	}

	private void addItem(String value) {
		entries.addItem(new Object[] {value}, value);
	}

	protected void deleteEntry(Table table) {
		if (table.getValue() != null) {
			String valueToDelete = (String) table.getValue();
			table.removeItem(valueToDelete);
			@SuppressWarnings("unchecked")
			List<String> values = (List<String>) article.get(attributeName);
			if (values.contains(valueToDelete)) {
				values.remove(valueToDelete);
				article.put(attributeName, values);
			}
		}
	}
	protected void addEntry(Table table) {
		RuleEditor editor = new RuleEditor(article, "nicki.editor.catalogs.rule.new");
		editor.setHandler(new RuleHandler());
		Window newWindow = new Window(
				I18n.getText("nicki.editor.catalogs.rule.new.window.title"), editor);
		newWindow.setWidth(440, Sizeable.UNITS_PIXELS);
		newWindow.setHeight(500, Sizeable.UNITS_PIXELS);
		newWindow.setModal(true);
		mainLayout.getWindow().addWindow(newWindow);
	}

	public class RuleHandler {
		
		public void setRule(Selector selector, String value) {
			String entry = selector.getName() + "=" + value;
			addItem(entry);
			@SuppressWarnings("unchecked")
			List<String> values = (List<String>) article.get(attributeName);
			if (!values.contains(entry)) {
				values.add(entry);
				article.put(attributeName, values);
			}
		}
	}

	
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
		
		// testButton
		testButton = new Button();
		testButton.setWidth("-1px");
		testButton.setHeight("-1px");
		testButton.setCaption("Test");
		testButton.setImmediate(false);
		mainLayout.addComponent(testButton,
				"top:100.0px;left:420.0px;");
		
		return mainLayout;
	}
}
