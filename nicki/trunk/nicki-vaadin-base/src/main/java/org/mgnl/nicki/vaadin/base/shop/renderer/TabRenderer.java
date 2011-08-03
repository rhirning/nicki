package org.mgnl.nicki.vaadin.base.shop.renderer;

import java.util.Date;
import java.util.Iterator;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.shop.catalog.CatalogArticle;
import org.mgnl.nicki.vaadin.base.editor.Icon;
import org.mgnl.nicki.vaadin.base.shop.core.ShopPage;
import org.mgnl.nicki.vaadin.base.shop.core.ShopViewerComponent;
import org.mgnl.nicki.vaadin.base.shop.core.ShopPage.TYPE;
import org.mgnl.nicki.vaadin.base.shop.inventory.Inventory;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class TabRenderer extends BaseShopRenderer implements ShopRenderer {

	private ShopViewerComponent shopViewerComponent;
	@Override
	public Component render(ShopViewerComponent shopViewerComponent, Inventory inventory) {
		this.shopViewerComponent = shopViewerComponent;
		setInventory(inventory);
		TabSheet tabSheet = new TabSheet();
		tabSheet.setHeight("100%");
		addTabs(tabSheet);
		return tabSheet;
	}
	
	private void addTabs(TabSheet tabSheet) {
		for (Iterator<CatalogArticle> iterator = shopViewerComponent.getArticles().iterator(); iterator.hasNext();) {
			CatalogArticle article = iterator.next();
			addArticleTab(tabSheet, article);
		}
		for (Iterator<ShopPage> iterator = shopViewerComponent.getPageList().iterator(); iterator.hasNext();) {
			ShopPage page = iterator.next();
			addPageTab(tabSheet, page);
		}
	}
	
	private void addPageTab(TabSheet tabSheet, ShopPage page) {
		if (page.getType() == TYPE.TYPE_SHOP_ARTICLE_PAGE) {
			tabSheet.addTab(getSingleArticlePageComponent(page), page.getLabel(), Icon.SETTINGS.getResource());
		} else if (page.getType() == TYPE.TYPE_PAGE_REF_PAGE) {
			ShopRenderer renderer = null;
			try {
				renderer = (ShopRenderer) Class.forName(page.getRenderer()).newInstance();
			} catch (Exception e) {
				renderer = new TabRenderer();
			}
			tabSheet.addTab(renderer.render(page, getInventory()), page.getLabel(), Icon.SETTINGS.getResource());
		} else if (page.getType() == TYPE.TYPE_STRUCTURE_PAGE) {
			ShopRenderer renderer = null;
			try {
				renderer = (ShopRenderer) Class.forName(page.getRenderer()).newInstance();
			} catch (Exception e) {
				renderer = new TabRenderer();
			}
			tabSheet.addTab(renderer.render(page, getInventory()), page.getLabel(), Icon.SETTINGS.getResource());
		}

	}
	
	private void addArticleTab(TabSheet tabSheet, CatalogArticle article) {
		tabSheet.addTab(getArticleComponent(article), article.getDisplayName(), Icon.SETTINGS.getResource());
		
	}


	// TODO: use renderer
	private Component getSingleArticlePageComponent(ShopPage page) {
		if (page.hasArticles()) {
			return getArticleComponent(page.getArticleList().get(0));
		}
		return null;
	}

	protected Component getArticleComponent(CatalogArticle article) {
		AbsoluteLayout layout = new AbsoluteLayout();
		layout.setData(article);
		layout.setHeight("420px");
		CheckBox checkBox = new CheckBox(I18n.getText("nicki.rights.checkbox.label"));
		checkBox.setImmediate(true);
		layout.addComponent(checkBox, "top:20.0px;left:20.0px;right:20.0px;");
		checkBox.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				  boolean checked = event.getButton().booleanValue();
				  AbsoluteLayout parent = (AbsoluteLayout) event.getButton().getParent();
				  CatalogArticle article = (CatalogArticle) parent.getData(); 
				  if (checked) {
					  getInventory().addArticle(article);
					  // TODO
					  boolean provisioned = false;
					  parent.addComponent(getVerticalArticleAttributes(article, provisioned), "top:60.0px;left:20.0px;right:20.0px;");
				  } else {
					  getInventory().removeArticle(article);
					  removeExcept(parent, event.getButton());
				  }
			}
		});

		if (getInventory().hasArticle(article)) {
			checkBox.setValue(true);
			checkBox.setEnabled(false);
			// TODO
			boolean provisioned = true;
			layout.addComponent(getVerticalArticleAttributes(article, provisioned), "top:60.0px;left:20.0px;right:20.0px;");
		}
		return layout;
	}
	

	@Override
	protected AbstractOrderedLayout getHorizontalArticleAttributes(
			CatalogArticle article, boolean enabled) {
		AbstractOrderedLayout layout = super.getHorizontalArticleAttributes(article, enabled);
		layout.addComponentAsFirst(getAttributeComponent(article, CatalogArticle.getFixedAttribute("dateTo"), enabled));
		layout.addComponentAsFirst(getAttributeComponent(article, CatalogArticle.getFixedAttribute("dateFrom"), enabled, new Date()));

		return layout;
	}

	@Override
	protected AbstractOrderedLayout getVerticalArticleAttributes(
			CatalogArticle article, boolean provisioned) {
		AbstractOrderedLayout layout =  super.getVerticalArticleAttributes(article, provisioned);
		boolean enabled = true;
		layout.addComponentAsFirst(getAttributeComponent(article, CatalogArticle.getFixedAttribute("dateTo"), enabled));
		layout.addComponentAsFirst(getAttributeComponent(article, CatalogArticle.getFixedAttribute("dateFrom"), !provisioned, new Date()));

		return layout;
	}

}
