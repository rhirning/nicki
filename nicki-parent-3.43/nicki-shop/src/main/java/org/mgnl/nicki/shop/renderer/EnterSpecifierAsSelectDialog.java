/**
 * Copyright (c) 2003-2015 Dr. Ralf Hirning
 * All rights reserved.
 *  
 * This program is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 * 
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU Public License v3.0
 * which is available at
 * http://www.gnu.org/licenses/gpl.html
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 * 
 * Any modifications to this file must keep this entire header
 * intact.
*/
package org.mgnl.nicki.shop.renderer;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.shop.base.objects.CatalogValueProvider;
import org.mgnl.nicki.shop.base.objects.MultipleInstancesCatalogArticle;
import org.mgnl.nicki.vaadin.base.components.DialogBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Item;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;

@SuppressWarnings("serial")
public class EnterSpecifierAsSelectDialog extends DialogBase implements NewItemHandler {
	private static final Logger LOG = LoggerFactory.getLogger(EnterSpecifierAsSelectDialog.class);

	@AutoGenerated
	private AbsoluteLayout mainLayout;

	@AutoGenerated
	private HorizontalLayout horizontalLayout_1;

	@AutoGenerated
	private Button closeButton;

	@AutoGenerated
	private Button createButton;

	@AutoGenerated
	private ComboBox name;

	@AutoGenerated
	private Label headline;
	
	private String i18nBase;

	private CreateInstanceHandler handler;
	
	private CatalogValueProvider provider;
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	
	public EnterSpecifierAsSelectDialog(String messageBase, String title) {
		super(title);
		i18nBase = messageBase;
		buildMainLayout();
		setCompositionRoot(mainLayout);

		name.focus();
		applyI18n(messageBase);
		
		createButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					String value = (String) name.getValue();
					if (!provider.checkEntry(value)) {
						Notification.show(I18n.getText(i18nBase + ".error"));
					} else {
						getHandler().setName(value);
						close();
					}
				} catch (Exception e) {
					LOG.error("Error", e);
					Notification.show(I18n.getText(i18nBase + ".error"),
							e.getMessage(), Notification.Type.ERROR_MESSAGE);
				}
			}
		});
		
		closeButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		
		createButton.setClickShortcut(KeyCode.ENTER);
	}
	
	public void init(MultipleInstancesCatalogArticle catalogArticle) {
		provider = catalogArticle.getValueProvider();
		for (String value : provider.getEntries().keySet()) {
			Item item = name.addItem(value);
			name.setItemCaption(item, provider.getEntries().get(value));
		}
		if (provider.isOnlyDefinedEntries()) {
			name.setNewItemsAllowed(false);
		} else {
			name.setNewItemsAllowed(true);
			name.setNewItemHandler(this);
		}
	}

	private void applyI18n(String messageBase) {
		headline.setValue(I18n.getText(messageBase + ".headline"));
		createButton.setCaption(I18n.getText(messageBase + ".button.create"));
		closeButton.setCaption(I18n.getText(messageBase + ".button.close"));
	}
		
	public CreateInstanceHandler getHandler() {
		return handler;
	}
	
	public void setName(String name) {
		this.name.setValue(name);
	}

	public void setHandler(CreateInstanceHandler handler) {
		this.handler = handler;
//		handler.setDialog(this);
	}

	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// headline
		headline = new Label();
		headline.setWidth("400px");
		headline.setHeight("-1px");
		headline.setValue("Headline");
		headline.setImmediate(false);
		mainLayout.addComponent(headline, "top:20.0px;left:20.0px;");
		
		// name
		name = new ComboBox();
		name.setWidth("200px");
		name.setHeight("-1px");
		name.setImmediate(true);
		mainLayout.addComponent(name, "top:60.0px;left:20.0px;");
		
		// horizontalLayout_1
		horizontalLayout_1 = buildHorizontalLayout_1();
		mainLayout.addComponent(horizontalLayout_1, "top:100.0px;left:20.0px;");
		
		return mainLayout;
	}

	@AutoGenerated
	private HorizontalLayout buildHorizontalLayout_1() {
		// common part: create layout
		horizontalLayout_1 = new HorizontalLayout();
		horizontalLayout_1.setWidth("-1px");
		horizontalLayout_1.setHeight("-1px");
		horizontalLayout_1.setImmediate(false);
		horizontalLayout_1.setMargin(false);
		
		// createButton
		createButton = new Button();
		createButton.setWidth("-1px");
		createButton.setHeight("-1px");
		createButton.setCaption("Create");
		createButton.setImmediate(true);
		horizontalLayout_1.addComponent(createButton);
		
		// closeButton
		closeButton = new Button();
		closeButton.setWidth("-1px");
		closeButton.setHeight("-1px");
		closeButton.setCaption("Close");
		closeButton.setImmediate(true);
		horizontalLayout_1.addComponent(closeButton);
		
		return horizontalLayout_1;
	}
	
	public void addNewItem(String newItemCaption) {
        if (!name.containsId(newItemCaption)) {
        	name.addItem(newItemCaption);
        	name.setValue(newItemCaption);
        }
	}
}
