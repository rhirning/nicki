/*******************************************************************************
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
 ******************************************************************************/
package org.mgnl.nicki.shop.renderer;


import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.inventory.Inventory;
import org.mgnl.nicki.shop.base.inventory.InventoryArticle;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;


public class CheckBoxChangeListener implements Property.ValueChangeListener {
	private static final long serialVersionUID = 5397184007544830202L;
	private Inventory inventory;
	private CatalogArticle catalogArticle;
	private TableRenderer tableRenderer;
	public CheckBoxChangeListener(Inventory inventory,
			CatalogArticle catalogArticle, TableRenderer tableRenderer) {
		super();
		this.inventory = inventory;
		this.catalogArticle = catalogArticle;
		this.tableRenderer = tableRenderer;
	}




	@Override
	public void valueChange(ValueChangeEvent event) {
		Item item = tableRenderer.getTable().getItem(catalogArticle);
		String checkedString = String.valueOf(event.getProperty().getValue());
		boolean checked = DataHelper.booleanOf(checkedString);
		if (checked) {
			InventoryArticle inventoryArticle = inventory
					.addArticle(catalogArticle);
			tableRenderer.showEntry(item, catalogArticle, inventoryArticle);
		} else {
			inventory.removeArticle(catalogArticle);
			tableRenderer.hideEntry(item);
		}
		tableRenderer.handleChange();
	}

}
