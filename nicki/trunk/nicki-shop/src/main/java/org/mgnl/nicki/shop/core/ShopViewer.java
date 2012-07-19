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
package org.mgnl.nicki.shop.core;


import java.io.Serializable;
import java.util.List;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.shop.CatalogArticle;
import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.shop.inventory.Inventory;
import org.mgnl.nicki.shop.renderer.ShopRenderer;
import org.mgnl.nicki.shop.renderer.TabRenderer;
import org.mgnl.nicki.vaadin.base.command.SelectPersonCommand;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class ShopViewer extends CustomComponent implements ShopViewerComponent, Serializable {

	private AbsoluteLayout mainLayout;
	private Person user;
	private Person person = null;
	private Inventory inventory = null;
	private Shop shop;
	private Button saveButton;
	private PersonSelector personSelector;
	private ShopRenderer renderer = null;
	private ShopParent parent;
	
	public ShopViewer(Person user, Shop shop, PersonSelector personSelector, ShopParent parent) {
		this.user = user;
		this.shop = shop;
		this.personSelector = personSelector;
		this.parent = parent;
		init();
	}


	public ShopViewer(Person user, Shop shop, Person person, ShopParent parent) throws InvalidPrincipalException, InstantiateDynamicObjectException {
		this.user = user;
		this.shop = shop;
		this.person = person;
		this.parent = parent;
		this.inventory = new Inventory(user, person);
		init();
	}
	
	public void init() {
		if (shop.getRenderer() != null) {
			try {
				this.renderer = (ShopRenderer) Classes.newInstance(shop.getRenderer());
			} catch (Exception e) {
				e.printStackTrace();
				this.renderer = null;
			}
		}
		if (this.renderer == null) {
			this.renderer = new TabRenderer();
		}
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}


	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		if (person != null) {
			showShop();
		} else {
			Button selectPerson = new Button(I18n.getText("nicki.editor.generic.button.selectPerson"));
			selectPerson.addListener(new Button.ClickListener() {
				
				public void buttonClick(ClickEvent event) {
					boolean useInternalExternal = true;
					boolean useActiveInactive = true;
					personSelector.init(user.getContext(), useInternalExternal, useActiveInactive, new SelectPersonCommand() {
						
						public void setSelectedPerson(Person selectedPerson) {
							try {
								person = selectedPerson;
								inventory = new Inventory(user, person);
								mainLayout.removeAllComponents();
								showShop();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
	
					});
					Window window = new Window(I18n.getText(parent.getI18nBase() + ".window.person.title"), personSelector);
					window.setWidth(640, Sizeable.UNITS_PIXELS);
					window.setHeight(640, Sizeable.UNITS_PIXELS);
					window.setModal(true);
	
					getWindow().addWindow(window);
				}
			});
			
			mainLayout.addComponent(selectPerson, "top:20.0px;left:20.0px;");
		}
		return mainLayout;
		
	}
	
	private void showShop() {
		String shopPosition = "top:0.0px;left:0.0px;";
		if (personSelector != null) {
			AbsoluteLayout personLayout = new AbsoluteLayout();
			personLayout.setHeight("30px");
			personLayout.setWidth("100%");
			Label personLabel = new Label(person.getDisplayName());
			personLabel.setWidth("200px");
			personLayout.addComponent(personLabel, "top:0.0px;left:20.0px;");
	
			Button selectPerson = new Button();
			selectPerson.setWidth("-1px");
			selectPerson.setHeight("-1px");
			selectPerson.setCaption("Auswählen");
			selectPerson.setImmediate(false);
			personLayout.addComponent(selectPerson, "top:0.0px;left:240.0px;");
	
			mainLayout.addComponent(personLayout, "top:0.0px;left:0.0px;");
			shopPosition = "top:30.0px;left:0.0px;";
		}		
		AbsoluteLayout layout = new AbsoluteLayout();
		layout.setHeight("100%");
		mainLayout.addComponent(layout, shopPosition);
		

		saveButton = new Button(I18n.getText("nicki.editor.generic.button.save"));
		saveButton.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				if (getInventory() != null) {
					// System.out.println(getInventory().toString());
					try {
						if (!getInventory().hasChanged()) {
							getWindow().showNotification(I18n.getText(parent.getI18nBase() + ".save.empty"),
									Notification.TYPE_HUMANIZED_MESSAGE);
						} else {
							getInventory().save("shop");
							getWindow().showNotification(I18n.getText(parent.getI18nBase() + ".save.success"),
									Notification.TYPE_HUMANIZED_MESSAGE);
							parent.closeShop();
						}
					} catch (Exception e) {
						getWindow().showNotification(I18n.getText(parent.getI18nBase() + ".save.error"),
								e.getMessage(),
								Notification.TYPE_ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			}
		});

		layout.addComponent(renderer.render(getShopViewerComponent(), getInventory()), "top:0.0px;bottom:30.0px;left:0.0px;");
		layout.addComponent(saveButton, "bottom:0.0px;left:20.0px;");
	}


	public ShopViewerComponent getShopViewerComponent() {
		return this;
	}


	public List<ShopPage> getPageList() {
		return this.shop.getPageList();
	}


	public List<CatalogArticle> getArticles() {
		return shop.getArticles();
	}

	public List<CatalogArticle> getAllArticles() {
		return shop.getAllArticles();
	}


	public Inventory getInventory() {
		return inventory;
	}


}
