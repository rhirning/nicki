package org.mgnl.nicki.vaadin.base.shop;


import java.io.Serializable;
import java.util.List;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.CatalogArticle;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.vaadin.base.command.SelectPersonCommand;
import org.mgnl.nicki.vaadin.base.renderer.ShopRenderer;
import org.mgnl.nicki.vaadin.base.renderer.TabRenderer;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;


@SuppressWarnings("serial")
public class ShopViewer extends CustomComponent implements ShopViewerComponent, Serializable {

	private AbsoluteLayout mainLayout;
	private Person user;
	private Person person;
	private Shop shop;
	private Button saveButton;
	private PersonSelector personSelector;
	private ShopRenderer renderer = null;
	
	public ShopViewer(Person user, Shop shop, PersonSelector personSelector) {
		this.user = user;
		this.shop = shop;
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
						showShop();
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
	
	private void showShop() {
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
		
		layout.addComponent(renderer.render(getShopViewerComponent()));

		saveButton = new Button(I18n.getText("nicki.editor.generic.button.save"));
		saveButton.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				// save();
			}
		});

		layout.addComponent(saveButton);
	}
	



	public void setPerson(Person person) {
		this.person = person;
	}


	@Override
	public ShopViewerComponent getShopViewerComponent() {
		return this;
	}


	@Override
	public List<ShopPage> getPageList() {
		return this.shop.getPageList();
	}


	@Override
	public List<CatalogArticle> getArticles() {
		return shop.getArticles();
	}

	@Override
	public List<CatalogArticle> getAllArticles() {
		return shop.getAllArticles();
	}


}
