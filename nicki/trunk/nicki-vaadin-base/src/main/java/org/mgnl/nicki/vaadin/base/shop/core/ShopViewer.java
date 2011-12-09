/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.shop.core;


import java.io.Serializable;
import java.util.List;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.shop.catalog.CatalogArticle;
import org.mgnl.nicki.vaadin.base.command.SelectPersonCommand;
import org.mgnl.nicki.vaadin.base.shop.inventory.Inventory;
import org.mgnl.nicki.vaadin.base.shop.renderer.ShopRenderer;
import org.mgnl.nicki.vaadin.base.shop.renderer.TabRenderer;

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


	public ShopViewer(Person user, Shop shop, Person person, ShopParent parent) {
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
				this.renderer = (ShopRenderer) Class.forName(shop.getRenderer()).newInstance();
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
					boolean useActiveInactive = true;
					personSelector.init(user.getContext(), useActiveInactive, new SelectPersonCommand() {
						
						public void setSelectedPerson(Person selectedPerson) {
							person = selectedPerson;
							inventory = new Inventory(user, person);
							mainLayout.removeAllComponents();
							showShop();
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
							getInventory().save();
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
