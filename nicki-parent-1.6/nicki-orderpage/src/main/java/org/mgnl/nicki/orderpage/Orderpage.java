package org.mgnl.nicki.orderpage;


import java.io.Serializable;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Org;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.objects.Shop;
import org.mgnl.nicki.dynamic.objects.objects.ShopArticle;
import org.mgnl.nicki.dynamic.objects.objects.ShopShelf;
import org.mgnl.nicki.ldap.context.Target;
import org.mgnl.nicki.ldap.context.TargetFactory;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.shop.dialog.ShopWindow;
import org.mgnl.nicki.vaadin.base.application.NickiApplication;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectRoot;
import org.mgnl.nicki.vaadin.base.editor.Icon;
import org.mgnl.nicki.vaadin.base.editor.ShowAllFilter;
import org.mgnl.nicki.vaadin.base.editor.TreeAction;
import org.mgnl.nicki.vaadin.base.editor.DataProvider;
import org.mgnl.nicki.vaadin.base.editor.TreeEditor;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class Orderpage extends NickiApplication {

	@Override
	public Component getEditor() {
		DataProvider treeDataProvider = new DynamicObjectRoot(Config.getProperty("nicki.shops.basedn"), new ShowAllFilter());
		TreeEditor editor = new TreeEditor(this, getNickiContext(), treeDataProvider, getI18nBase());
		editor.configureClass(Org.class, null, TreeEditor.DELETE.DENY, TreeEditor.RENAME.DENY, Shop.class);
		editor.configureClass(Shop.class, Icon.FOLDER, TreeEditor.DELETE.ALLOW, TreeEditor.RENAME.ALLOW, ShopShelf.class);
		editor.configureClass(ShopShelf.class, Icon.FOLDER, TreeEditor.DELETE.ALLOW, TreeEditor.RENAME.ALLOW, ShopShelf.class, ShopArticle.class);
		editor.configureClass(ShopArticle.class, Icon.DOCUMENT, TreeEditor.DELETE.ALLOW, TreeEditor.RENAME.ALLOW);
		editor.addAction(new PreviewShop(Shop.class, I18n.getText(getI18nBase() +  ".action.preview")));
		editor.initActions();
		return editor;
	}

	public class PreviewShop implements TreeAction, Serializable {

		private Class<?> targetClass;
		private String name;
		private Window previewWindow;
		public PreviewShop(Class<?> classDefinition, String name) {
			this.targetClass = classDefinition;
			this.name = name;
		}

		public void execute(Window parentWindow, DynamicObject dynamicObject) {
			 previewWindow = new Window(I18n.getText(getI18nBase() + ".preview.window.title"),
					new ShopWindow(this, new Person(), (Shop)dynamicObject, getI18nBase()));
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

		public Class<?> getTargetClass() {
			return targetClass;
		}
		
	}

	@Override
	public Target getTarget() {
		return TargetFactory.getDefaultTarget();
	}

	@Override
	public String getI18nBase() {
		return "nicki.editor.shops";
	}
}
