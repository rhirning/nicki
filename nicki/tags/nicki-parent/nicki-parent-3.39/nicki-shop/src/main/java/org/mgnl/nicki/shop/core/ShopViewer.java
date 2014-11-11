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

import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.core.util.Classes;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.shop.base.objects.Cart;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.objects.Cart.CART_STATUS;
import org.mgnl.nicki.shop.base.inventory.Inventory;
import org.mgnl.nicki.shop.renderer.ShopRenderer;
import org.mgnl.nicki.shop.renderer.TabRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class ShopViewer extends CustomComponent implements ShopViewerComponent, Serializable {
	private static final Logger LOG = LoggerFactory.getLogger(ShopViewer.class);

	private Person shopper;
	private Person recipient = null;
	private Inventory inventory = null;
	private Shop shop;
	private Button saveButton;
	private Button rememberButton;
	private Button showInventoryButton;
	private Button showCartButton;
	private ShopRenderer renderer = null;
	private ShopParent parent;
	private Cart cart = null;

	public ShopViewer(Person user, Shop shop, Person recipient, ShopParent parent, Cart cart) throws InvalidPrincipalException, InstantiateDynamicObjectException {
		this.setShopper(user);
		this.shop = shop;
		this.setRecipient(recipient);
		this.parent = parent;
		this.setCart(cart);
		if (cart != null) {
			this.inventory = Inventory.fromCart(user, recipient, cart);
		} else {
			this.inventory = new Inventory(user, recipient);
		}
		init();
	}
	
	public void init() {
		if (shop.getRenderer() != null) {
			try {
				this.renderer = (ShopRenderer) Classes.newInstance(shop.getRenderer());
			} catch (Exception e) {
				LOG.error("Error", e);
				this.renderer = null;
			}
		}
		if (this.renderer == null) {
			this.renderer = new TabRenderer();
		}
		setCompositionRoot(getShop());
	}
	
	private VerticalLayout getShop() {
		VerticalLayout shopLayout = new VerticalLayout();
		AbsoluteLayout layout = new AbsoluteLayout();
		layout.setHeight(24, Unit.PIXELS);
		layout.setWidth("100%");

		saveButton = new Button(I18n.getText("nicki.editor.generic.button.save"));
		saveButton.addClickListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				if (getInventory() != null) {
					// LOG.debug(getInventory().toString());
					try {
						if (!getInventory().hasChanged()) {
							Notification.show(I18n.getText(parent.getI18nBase() + ".save.empty"),
									Notification.Type.HUMANIZED_MESSAGE);
						} else {
							setCart(getInventory().save("shop", getCart()));
							Notification.show(I18n.getText(parent.getI18nBase() + ".save.success"),
									Notification.Type.HUMANIZED_MESSAGE);
							parent.closeShop();
						}
					} catch (Exception e) {
						Notification.show(I18n.getText(parent.getI18nBase() + ".save.error"),
								e.getMessage(),
								Notification.Type.ERROR_MESSAGE);
						LOG.error("Error", e);
					}
				}
			}
		});
		

		rememberButton = new Button(I18n.getText("nicki.editor.generic.button.remember"));
		rememberButton.addClickListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				if (getInventory() != null) {
					// LOG.debug(getInventory().toString());
					try {
						if (!getInventory().hasChanged()) {
							Notification.show(I18n.getText(parent.getI18nBase() + ".remember.empty"),
									Notification.Type.HUMANIZED_MESSAGE);
						} else {
							setCart(getInventory().remember("shop", getCart()));
							Notification.show(I18n.getText(parent.getI18nBase() + ".remember.success"),
									Notification.Type.HUMANIZED_MESSAGE);
							parent.closeShop();
						}
					} catch (Exception e) {
						Notification.show(I18n.getText(parent.getI18nBase() + ".remember.error"),
								e.getMessage(),
								Notification.Type.ERROR_MESSAGE);
						LOG.error("Error", e);
					}
				}
			}
		});
		

		showInventoryButton = new Button(I18n.getText("nicki.editor.generic.button.showInventory"));
		showInventoryButton.addClickListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				if (getInventory() != null) {
					InventoryViewer inventoryViewer = new InventoryViewer(getInventory());
					Window newWindow = new Window(I18n.getText(parent.getI18nBase() + ".inventory.window.title"), inventoryViewer);
					newWindow.setWidth(1000, Unit.PIXELS);
					newWindow.setHeight(600, Unit.PIXELS);
					newWindow.setModal(true);
					UI.getCurrent().addWindow(newWindow);
				} else {
					Notification.show(I18n.getText(parent.getI18nBase() + ".showInventory.empty"),
							Notification.Type.HUMANIZED_MESSAGE);
				}
			}
		});

		showCartButton = new Button(I18n.getText("nicki.editor.generic.button.showCart"));
		showCartButton.addClickListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				if (getInventory() != null) {
					if (!getInventory().hasChanged()) {
						Notification.show(I18n.getText(parent.getI18nBase() + ".showCart.empty"),
								Notification.Type.HUMANIZED_MESSAGE);
					} else {
						CartViewer cartViewer = new CartViewer(getInventory().getCart("shop", CART_STATUS.NEW));
						Window newWindow = new Window(I18n.getText(parent.getI18nBase() + ".cart.window.title"), cartViewer);
						newWindow.setWidth(1000, Unit.PIXELS);
						newWindow.setHeight(600, Unit.PIXELS);
						newWindow.setModal(true);
						UI.getCurrent().addWindow(newWindow);
						/*
						getWindow().showNotification(I18n.getText(parent.getI18nBase() + ".showCart.success"), getInventory().toString(),
								Notification.TYPE_HUMANIZED_MESSAGE);
						*/
					}
				} else {
					Notification.show(I18n.getText(parent.getI18nBase() + ".showCart.empty"),
							Notification.Type.HUMANIZED_MESSAGE);
				}
			}
		});

		layout.addComponent(saveButton, "top:0.0px;left:20.0px;");
		layout.addComponent(rememberButton, "top:0.0px;left:220.0px;");
		layout.addComponent(showInventoryButton, "top:0.0px;right:200.0px;");
		layout.addComponent(showCartButton, "top:0.0px;right:20.0px;");
		shopLayout.addComponent(layout);
		Component shopComponent = renderer.render(getShopViewerComponent(), getInventory());
		shopLayout.addComponent(shopComponent);
		shopComponent.setSizeFull();
		shopLayout.setExpandRatio(shopComponent, 1);
		return shopLayout;
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


	public Cart getCart() {
		return cart;
	}


	public void setCart(Cart cart) {
		this.cart = cart;
	}

	public Person getRecipient() {
		return recipient;
	}

	public void setRecipient(Person recipient) {
		this.recipient = recipient;
	}

	public Person getShopper() {
		return shopper;
	}

	public void setShopper(Person shopper) {
		this.shopper = shopper;
	}


}
