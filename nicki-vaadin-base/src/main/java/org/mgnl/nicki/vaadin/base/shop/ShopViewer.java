package org.mgnl.nicki.vaadin.base.shop;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.CatalogArticle;
import org.mgnl.nicki.dynamic.objects.objects.CatalogArticleAttribute;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.shop.core.Shop;
import org.mgnl.nicki.shop.core.ShopPage;
import org.mgnl.nicki.shop.core.ShopPage.TYPE;
import org.mgnl.nicki.vaadin.base.command.SelectPersonCommand;
import org.mgnl.nicki.vaadin.base.editor.Icon;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;


@SuppressWarnings("serial")
public class ShopViewer extends CustomComponent implements Serializable {

	private AbsoluteLayout mainLayout;
	private Person user;
	private Person person;
	private Shop shop;
	private Button saveButton;
	private DynamicObject parent = null;
	private PersonSelector personSelector;
	
	private static Map<String, AttributeComponent> attributeComponents = new HashMap<String, AttributeComponent>();
	static {
		attributeComponents.put("date", new DateComponent());
		attributeComponents.put("text", new TextComponent());
		attributeComponents.put("checkbox", new CheckboxComponent());
		attributeComponents.put("costCenter", new CostCenterComponent());
		attributeComponents.put("default", new LabelComponent());
	}


	public ShopViewer(Person user, Shop shop, PersonSelector personSelector) {
		this.user = user;
		this.shop = shop;
		this.personSelector = personSelector;
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}


	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		Button selectPerson = new Button(I18n.getText("nicki.editor.generic.button.selectPerson"));
		selectPerson.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				personSelector.init(user.getContext(), new SelectPersonCommand() {
					
					@Override
					public void setSelectedPerson(Person selectedPerson) {
						setPerson(selectedPerson);
						mainLayout.removeAllComponents();
						showTabs();
					}

				});
				Window window = new Window(I18n.getText("nicki.editor.bu.ouchange.window.person.title"), personSelector);
				window.setWidth(640, Sizeable.UNITS_PIXELS);
				window.setHeight(640, Sizeable.UNITS_PIXELS);
				window.setModal(true);

				getWindow().addWindow(window);
			}
		});
		
		mainLayout.addComponent(selectPerson, "top:20.0px;left:20.0px;");
		return mainLayout;
		
	}
	
	private void showTabs() {
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
		
		VerticalLayout layout = new VerticalLayout();
		mainLayout.addComponent(layout, "top:30.0px;left:0.0px;");
		
		TabSheet tabSheet = new TabSheet();
		tabSheet.setHeight(480, UNITS_PIXELS);

		layout.addComponent(tabSheet);
		
		addTabs(tabSheet);

		saveButton = new Button(I18n.getText("nicki.editor.generic.button.save"));
		saveButton.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				// save();
			}
		});

		layout.addComponent(saveButton);
	}
	
	private void addTabs(TabSheet tabSheet) {
		for (Iterator<ShopPage> iterator = shop.getPageList().iterator(); iterator.hasNext();) {
			ShopPage page = iterator.next();
			addPageTab(tabSheet, page);
		}
		tabSheet.addTab(getXMLComponent(), "XML", Icon.DOCUMENT.getResource());
	}


	private void addPageTab(TabSheet tabSheet, ShopPage page) {
		if (page.getType() == TYPE.TYPE_PERSON_DATA) {
			tabSheet.addTab(getPersonDataComponent(page), page.getLabel(), Icon.SETTINGS.getResource());
		} else if (page.getType() == TYPE.TYPE_SHOP_ARTICLE_PAGE) {
			tabSheet.addTab(getPageComponent(page), page.getLabel(), Icon.SETTINGS.getResource());
		} else if (page.getType() == TYPE.TYPE_STRUCTURE_PAGE) {
			tabSheet.addTab(getStructureComponent(page), page.getLabel(), Icon.SETTINGS.getResource());
		}

	}


	private Component getStructureComponent(ShopPage page) {
		TabSheet tabSheet = new TabSheet();
		tabSheet.setHeight(480, UNITS_PIXELS);
		
		if (page.hasArticles()) {
			addArticlesAsTabs(tabSheet, page);
		}
		return tabSheet;
	}

	private void addArticlesAsTabs(TabSheet tabSheet, ShopPage page) {
		for (Iterator<CatalogArticle> iterator = page.getArticleList().iterator(); iterator.hasNext();) {
			CatalogArticle article = iterator.next();
			if (article != null) {
				addArticleTab(tabSheet, article);
			}
		}
	}


	private void addArticleTab(TabSheet tabSheet, CatalogArticle article) {
		tabSheet.addTab(getArticleComponent(article), article.getDisplayName(), Icon.SETTINGS.getResource());
		
	}


	// TODO: use renderer
	private Component getPageComponent(ShopPage page) {
		if (page.hasArticles()) {
			return getArticleComponent(page.getArticleList().get(0));
		}
		return null;
	}


	private Component getPersonDataComponent(ShopPage page) {
		AbsoluteLayout layout = new AbsoluteLayout();
		layout.setData(page);
		layout.setHeight("420px");
		showPageAttributes(layout, "top:20.0px;left:20.0px;right:20.0px;");
		return layout;
	}

	private Component getArticleComponent(CatalogArticle article) {
		AbsoluteLayout layout = new AbsoluteLayout();
		layout.setData(article);
		layout.setHeight("420px");
		CheckBox checkBox = new CheckBox(I18n.getText("nicki.rights.checkbox.label"));
		checkBox.setImmediate(true);
		layout.addComponent(checkBox, "top:20.0px;left:20.0px;right:20.0px;");
		checkBox.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				  boolean enabled = event.getButton().booleanValue();
				  AbsoluteLayout parent = (AbsoluteLayout) event.getButton().getParent();
				  if (enabled) {
					  showArticleAttributes(parent, "top:60.0px;left:20.0px;right:20.0px;");
				  } else {
					  removeExcept(parent, event.getButton());
				  }
			}
		});

		return layout;
	}


	protected void removeExcept(AbsoluteLayout parent, Button button) {
		for (Iterator<Component> iterator = parent.getComponentIterator(); iterator.hasNext();) {
			Component component = iterator.next();
			if (component != button) {
				parent.removeComponent(component);
			}
		}
	}


	private void showArticleAttributes(AbsoluteLayout layout, String cssString) {
		CatalogArticle article = (CatalogArticle) layout.getData(); 
		VerticalLayout attrLayout = new VerticalLayout();
		
		if (article.hasAttributes()) {
			for (Iterator<CatalogArticleAttribute> iterator = article.getAllAttributes().iterator(); iterator.hasNext();) {
				CatalogArticleAttribute pageAttribute = iterator.next();
				attrLayout.addComponent(getAttributeComponent(pageAttribute));
			}
			layout.addComponent(attrLayout, cssString);
		}
	}

	private void showPageAttributes(AbsoluteLayout layout, String cssString) {
		ShopPage page = (ShopPage) layout.getData(); 
		VerticalLayout attrLayout = new VerticalLayout();
		
		if (page.hasAttributes()) {
			for (Iterator<CatalogArticleAttribute> iterator = page.getAttributeList().iterator(); iterator.hasNext();) {
				CatalogArticleAttribute pageAttribute = iterator.next();
				attrLayout.addComponent(getAttributeComponent(pageAttribute));
			}
			layout.addComponent(attrLayout, cssString);
		}
	}

	private Component getAttributeComponent(CatalogArticleAttribute pageAttribute) {
		if (attributeComponents.containsKey(pageAttribute.getType())) {
			return attributeComponents.get(pageAttribute.getType()).getInstance(pageAttribute);
		} else {
			return attributeComponents.get("default").getInstance(pageAttribute);
		}
	}


	private Component getXMLComponent() {
		AbsoluteLayout layout = new AbsoluteLayout();
		layout.setHeight("420px");
		// textArea_1
		TextArea xml = new TextArea();
		xml.setWidth("100%");
		xml.setHeight("100%");
		xml.setImmediate(false);
		xml.setValue(shop);
		layout.addComponent(xml, "top:20.0px;left:20.0px;right:20.0px;");

		return layout;
	}


	public void setPerson(Person person) {
		this.person = person;
		this.shop.setPerson(person);
	}
}
