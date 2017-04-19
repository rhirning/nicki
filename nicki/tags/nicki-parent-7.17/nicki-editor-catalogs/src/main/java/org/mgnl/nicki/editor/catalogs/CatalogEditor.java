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
package org.mgnl.nicki.editor.catalogs;


import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.context.TargetFactory;
import org.mgnl.nicki.core.data.DataProvider;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.dynamic.objects.objects.Org;
import org.mgnl.nicki.shop.base.objects.Catalog;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.objects.CatalogPage;
import org.mgnl.nicki.vaadin.base.application.AccessGroup;
import org.mgnl.nicki.vaadin.base.application.NickiApplication;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectRoot;
import org.mgnl.nicki.vaadin.base.editor.Icon;
import org.mgnl.nicki.vaadin.base.editor.ShowAllFilter;
import org.mgnl.nicki.vaadin.base.editor.TreeEditor;

import com.vaadin.ui.Component;

@AccessGroup(name = {"nickiAdmins", "IDM-Development"})
@SuppressWarnings("serial")
public class CatalogEditor extends NickiApplication {

	public CatalogEditor() {
		super();
		setUseWelcomeDialog(DataHelper.booleanOf(Config.getProperty("nicki.projects.useWelcomeDialog", "false")));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Component getEditor() {
		DataProvider treeDataProvider = new DynamicObjectRoot(Config.getProperty("nicki.catalogs.basedn"), new ShowAllFilter());
		TreeEditor editor = new TreeEditor(this, getNickiContext(), treeDataProvider, getI18nBase());
		editor.configureClass(Org.class, null, TreeEditor.CREATE.DENY, TreeEditor.DELETE.DENY, TreeEditor.RENAME.DENY, Catalog.class);
		editor.configureClass(Catalog.class, Icon.FOLDER, TreeEditor.CREATE.ALLOW, TreeEditor.DELETE.ALLOW, TreeEditor.RENAME.ALLOW, CatalogPage.class);
		editor.configureClass(CatalogPage.class, Icon.FOLDER, TreeEditor.CREATE.ALLOW, TreeEditor.DELETE.ALLOW, TreeEditor.RENAME.ALLOW, CatalogPage.class, CatalogArticle.class);
		editor.configureClass(CatalogArticle.class, Icon.DOCUMENT, TreeEditor.CREATE.ALLOW, TreeEditor.DELETE.ALLOW, TreeEditor.RENAME.ALLOW);
//		editor.addAction(new PreviewShop(Catalog.class, I18n.getText(getI18nBase() +  ".action.preview")));
//		editor.setClassEditor(CatalogPage.class, new CatalogPageViewer());
		editor.initActions();
		return editor;
	}

	/*
	public class PreviewShop implements TreeAction, Serializable {

		private Class<? extends DynamicObject> targetClass;
		private String name;
		private Window previewWindow;
		public PreviewShop(Class<? extends DynamicObject> classDefinition, String name) {
			this.targetClass = classDefinition;
			this.name = name;
		}

		public void execute(Window parentWindow, DynamicObject dynamicObject) {
			 previewWindow = new Window(I18n.getText(getI18nBase() + ".preview.window.title"),
					new ShopWindow(this, getNickiContext().getUser(), (Catalog)dynamicObject, getI18nBase()));
			 previewWindow.setModal(true);
			 previewWindow.setWidth(1024, Sizeable.UNITS_PIXELS);
			 previewWindow.setHeight(520, Sizeable.UNITS_PIXELS);
			 getMainWindow().addWindow(previewWindow);
		}

		public String getName() {
			return this.name;
		}

		public void close() {
			getMainWindow().removeWindow(previewWindow);
		}

		public Class<? extends DynamicObject> getTargetClass() {
			return targetClass;
		}
		
	}
	*/

	@Override
	public Target getTarget() {
		return TargetFactory.getDefaultTarget();
	}

	@Override
	public String getI18nBase() {
		return "nicki.editor.catalogs";
	}
}
