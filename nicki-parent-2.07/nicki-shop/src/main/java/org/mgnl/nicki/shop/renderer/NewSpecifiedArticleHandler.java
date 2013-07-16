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
package org.mgnl.nicki.shop.renderer;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.shop.objects.CatalogArticle;

import com.vaadin.ui.Window;

public class NewSpecifiedArticleHandler implements CreateInstanceHandler {

	private CatalogArticle catalogArticle;
	private TableRenderer tableRenderer;


	public NewSpecifiedArticleHandler(CatalogArticle catalogArticle, TableRenderer tableRenderer) {
		super();
		this.catalogArticle = catalogArticle;
		this.tableRenderer = tableRenderer;
	}

	@Override
	public void setName(String value) {
		if (tableRenderer.getInventory().hasArticle(catalogArticle, value)) {
			tableRenderer.getWindow().showNotification(I18n.getText("nicki.rights.specifier.exists"),
					Window.Notification.TYPE_ERROR_MESSAGE);
		} else {
			tableRenderer.getInventory().addArticle(catalogArticle, value);
			tableRenderer.render();
		}
	}

	@Override
	public String getName() {
		return "";
	}

}
