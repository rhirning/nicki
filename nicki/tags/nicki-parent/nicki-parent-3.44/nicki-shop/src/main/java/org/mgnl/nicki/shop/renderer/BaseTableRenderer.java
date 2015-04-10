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
import org.mgnl.nicki.shop.base.inventory.InventoryArticle;
import org.mgnl.nicki.shop.base.inventory.InventoryArticle.STATUS;
import org.mgnl.nicki.shop.base.objects.MultipleInstancesCatalogArticle;
import org.mgnl.nicki.shop.base.objects.CatalogValueProvider.TYPE;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public abstract class BaseTableRenderer extends BaseShopRenderer implements ShopRenderer {

	public static final Action EDIT_ACTION = new Action(I18n.getText("nicki.rights.attribute.action.edit"));

	public static final Action ACTIONS[] = {EDIT_ACTION};
	public static final Action NO_ACTIONS[] = {};

	
	public void editEntry(InventoryArticle iArticle) {
		if (iArticle.getArticle() instanceof MultipleInstancesCatalogArticle) {
			EditSpecifiedArticleHandler handler = new EditSpecifiedArticleHandler(iArticle, this);
			if (isTextArea(iArticle.getArticle())) {
				EnterSpecifierAsTextAreaDialog dialog = new EnterSpecifierAsTextAreaDialog("nicki.rights.specifier",
						I18n.getText("nicki.rights.specifier.define.window.title"));
				dialog.setHandler(handler);
				dialog.init((MultipleInstancesCatalogArticle) iArticle.getArticle());
				dialog.setName(iArticle.getSpecifier());
				dialog.setWidth(600, Unit.PIXELS);
				dialog.setHeight(560, Unit.PIXELS);
				dialog.setModal(true);
				UI.getCurrent().addWindow(dialog);
				
			} else {
				EnterSpecifierAsSelectDialog dialog = new EnterSpecifierAsSelectDialog("nicki.rights.specifier",
						I18n.getText("nicki.rights.specifier.define.window.title"));
				dialog.setHandler(handler);
				dialog.init((MultipleInstancesCatalogArticle) iArticle.getArticle());
				dialog.setName(iArticle.getSpecifier());
				dialog.setWidth(440, Unit.PIXELS);
				dialog.setHeight(500, Unit.PIXELS);
				dialog.setModal(true);
				UI.getCurrent().addWindow(dialog);
			}
		}
	}
	
	public boolean isTextArea(CatalogArticle catalogArticle) {
		if (catalogArticle instanceof MultipleInstancesCatalogArticle) {
			MultipleInstancesCatalogArticle micArticeCatalogArticle = (MultipleInstancesCatalogArticle) catalogArticle;
			if (micArticeCatalogArticle.getValueProvider() != null
					&& micArticeCatalogArticle.getValueProvider().getType() == TYPE.TEXT_AREA) {
				return true;
			}
		}
		return false;
	}
	
	public class ActionHandler implements Action.Handler {
		private static final long serialVersionUID = -9121430140825156577L;

		public void handleAction(Action action, Object sender, Object target) {
			if (action == EDIT_ACTION) {
				if (target instanceof InventoryArticle) {
					InventoryArticle iArticle = (InventoryArticle) target;
					editEntry(iArticle);
					
				}
			}
		}
		
		public Action[] getActions(Object target, Object sender) {
			if (target instanceof InventoryArticle) {
				InventoryArticle iArticle = (InventoryArticle) target;
				if (iArticle.getArticle() instanceof MultipleInstancesCatalogArticle) {
					if (iArticle.getStatus() == STATUS.NEW) {
						return ACTIONS;
					}
				}
			} 
			return NO_ACTIONS;
		}
	}

	public class ItemClickListener implements com.vaadin.event.ItemClickEvent.ItemClickListener {

		@Override
		public void itemClick(ItemClickEvent event) {
			if (event.isDoubleClick() && event.getItemId() instanceof InventoryArticle) {
				InventoryArticle iArticle = (InventoryArticle) event.getItemId();
				editEntry(iArticle);
			}
		}
		
	}

}
