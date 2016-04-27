package org.mgnl.nicki.editor.catalogs;

import java.util.Iterator;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.CatalogPage;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.components.EnterNameDialog;
import org.mgnl.nicki.vaadin.base.components.EnterNameHandler;
import org.mgnl.nicki.vaadin.base.editor.ClassEditor;
import org.mgnl.nicki.vaadin.base.editor.NickiTreeEditor;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class CatalogPageViewer extends CustomComponent implements ClassEditor {

	@AutoGenerated
	private AbsoluteLayout mainLayout;
	@AutoGenerated
	private Button saveButton;
	@AutoGenerated
	private Button deleteAttributeButton;
	@AutoGenerated
	private Button newAttributeButton;
	@AutoGenerated
	private Table attributes;
	@AutoGenerated
	private Button deleteCategoryButton;
	@AutoGenerated
	private Button newCategoryButton;
	@AutoGenerated
	private Table categories;
	
	private NickiTreeEditor editor;
	private CatalogPage page;

	public CatalogPageViewer() {
	}
	public void setDynamicObject(NickiTreeEditor nickiEditor, DynamicObject dynamicObject) {
		this.editor = nickiEditor;
		this.page = (CatalogPage) dynamicObject;
		buildMainLayout();
		setCompositionRoot(mainLayout);
		
		initCategories();
		initAttributes();
		
	}

	private void initCategories() {
		categories.setSelectable(true);
		categories.addContainerProperty("category", String.class, null);
		if (page.hasCategories()) {
			for (Iterator<String> iterator = page.getCategories().iterator(); iterator.hasNext();) {
				String value = iterator.next();
				categories.addItem(new Object[]{value}, value);
			}
		}
		newCategoryButton.addListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				addEntry(categories);
			}
		});
		deleteCategoryButton.addListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				deleteEntry(categories);
			}
		});
	}
	
	protected void deleteEntry(Table table) {
		if (table.getValue() != null) {
			table.removeItem(table.getValue());
		}
	}
	protected void addEntry(Table table) {
		EnterNameHandler handler = new NewEntryEnterNameHandler(table);
		EnterNameDialog dialog = new EnterNameDialog("nicki.editor.catalogs.entry.new");
		dialog.setHandler(handler);
		Window newWindow = new Window(
				I18n.getText("nicki.editor.catalogs.entry.new.window.title"), dialog);
		newWindow.setWidth(440, Sizeable.UNITS_PIXELS);
		newWindow.setHeight(500, Sizeable.UNITS_PIXELS);
		newWindow.setModal(true);
		this.getWindow().addWindow(newWindow);
	}


	private void initAttributes() {
		attributes.setSelectable(true);
		attributes.addContainerProperty("attribute", String.class, null);
		if (page.hasAttributes()) {
			for (Iterator<String> iterator = page.getCategories().iterator(); iterator.hasNext();) {
				String value = iterator.next();
				attributes.addItem(new Object[]{value}, value);
			}
		}
		newAttributeButton.addListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				addEntry(attributes);
			}
		});
		deleteAttributeButton.addListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				deleteEntry(attributes);
			}
		});
	}

	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// categories
		categories = new Table();
		categories.setWidth("400px");
		categories.setHeight("120px");
		categories.setCaption("Kategorien");
		categories.setImmediate(false);
		mainLayout.addComponent(categories, "top:20.0px;left:20.0px;");
		
		// newCategoryButton
		newCategoryButton = new Button();
		newCategoryButton.setWidth("-1px");
		newCategoryButton.setHeight("-1px");
		newCategoryButton.setCaption("Neu");
		newCategoryButton.setImmediate(false);
		mainLayout.addComponent(newCategoryButton, "top:20.0px;left:440.0px;");
		
		// deleteCategoryButton
		deleteCategoryButton = new Button();
		deleteCategoryButton.setWidth("-1px");
		deleteCategoryButton.setHeight("-1px");
		deleteCategoryButton.setCaption("L�schen");
		deleteCategoryButton.setImmediate(false);
		mainLayout.addComponent(deleteCategoryButton,
				"top:60.0px;left:440.0px;");
		
		// attributes
		attributes = new Table();
		attributes.setWidth("400px");
		attributes.setHeight("120px");
		attributes.setCaption("Attribute");
		attributes.setImmediate(false);
		mainLayout.addComponent(attributes, "top:180.0px;left:20.0px;");
		
		// newAttributeButton
		newAttributeButton = new Button();
		newAttributeButton.setWidth("-1px");
		newAttributeButton.setHeight("-1px");
		newAttributeButton.setCaption("Neu");
		newAttributeButton.setImmediate(false);
		mainLayout
				.addComponent(newAttributeButton, "top:180.0px;left:440.0px;");
		
		// deleteAttributeButton
		deleteAttributeButton = new Button();
		deleteAttributeButton.setWidth("-1px");
		deleteAttributeButton.setHeight("-1px");
		deleteAttributeButton.setCaption("L�schen");
		deleteAttributeButton.setImmediate(false);
		mainLayout.addComponent(deleteAttributeButton,
				"top:220.0px;left:440.0px;");
		
		// saveButton
		saveButton = new Button();
		saveButton.setWidth("-1px");
		saveButton.setHeight("-1px");
		saveButton.setCaption("Speichern");
		saveButton.setImmediate(false);
		mainLayout.addComponent(saveButton, "top:320.0px;left:20.0px;");
		
		return mainLayout;
	}

}