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
import org.mgnl.nicki.shop.base.objects.CatalogArticle;

import com.vaadin.ui.Notification;

public class NewSpecifiedArticleHandler implements CreateInstanceHandler {

	private CatalogArticle catalogArticle;
	private ShopRenderer renderer;


	public NewSpecifiedArticleHandler(CatalogArticle catalogArticle, ShopRenderer renderer) {
		super();
		this.catalogArticle = catalogArticle;
		this.renderer = renderer;
	}

	@Override
	public void setName(String value) {
		if (renderer.getInventory().hasArticle(catalogArticle, value)) {
			Notification.show(I18n.getText("nicki.rights.specifier.exists"),
					Notification.Type.ERROR_MESSAGE);
		} else {
			renderer.getInventory().addArticle(catalogArticle, value);
			renderer.handleChange();
		}
	}

	@Override
	public String getName() {
		return "";
	}

}