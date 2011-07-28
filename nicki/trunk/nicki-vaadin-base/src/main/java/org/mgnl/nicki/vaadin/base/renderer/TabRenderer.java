package org.mgnl.nicki.vaadin.base.renderer;

import java.util.Date;
import java.util.Iterator;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.CatalogArticle;
import org.mgnl.nicki.dynamic.objects.objects.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.editor.Icon;
import org.mgnl.nicki.vaadin.base.shop.ShopPage;
import org.mgnl.nicki.vaadin.base.shop.ShopPage.TYPE;
import org.mgnl.nicki.vaadin.base.shop.ShopViewerComponent;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class TabRenderer extends BaseShopRenderer implements ShopRenderer {

	private ShopViewerComponent shopViewerComponent;
	@Override
	public Component render(ShopViewerComponent shopViewerComponent) {
		this.shopViewerComponent = shopViewerComponent;
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
		if (page.getType() == TYPE.TYPE_PERSON_DATA) {
			tabSheet.addTab(getPersonDataComponent(page), page.getLabel(), Icon.SETTINGS.getResource());
		} else if (page.getType() == TYPE.TYPE_SHOP_ARTICLE_PAGE) {
			tabSheet.addTab(getPageComponent(page), page.getLabel(), Icon.SETTINGS.getResource());
		} else if (page.getType() == TYPE.TYPE_PAGE_REF_PAGE) {
			ShopRenderer renderer = null;
			try {
				renderer = (ShopRenderer) Class.forName(page.getRenderer()).newInstance();
			} catch (Exception e) {
				renderer = new TabRenderer();
			}
			tabSheet.addTab(renderer.render(page), page.getLabel(), Icon.SETTINGS.getResource());
		} else if (page.getType() == TYPE.TYPE_STRUCTURE_PAGE) {
			ShopRenderer renderer = null;
			try {
				renderer = (ShopRenderer) Class.forName(page.getRenderer()).newInstance();
			} catch (Exception e) {
				renderer = new TabRenderer();
			}
			tabSheet.addTab(renderer.render(page), page.getLabel(), Icon.SETTINGS.getResource());
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

	@SuppressWarnings("serial")
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
				  boolean enabled = event.getButton().booleanValue();
				  AbsoluteLayout parent = (AbsoluteLayout) event.getButton().getParent();
				  CatalogArticle article = (CatalogArticle) parent.getData(); 
				  if (enabled) {
					  parent.addComponent(getVerticalArticleAttributes(article), "top:60.0px;left:20.0px;right:20.0px;");
//					  showArticleAttributes(parent, "top:60.0px;left:20.0px;right:20.0px;");
				  } else {
					  removeExcept(parent, event.getButton());
				  }
			}
		});

		return layout;
	}
	
	
	private Component getPersonDataComponent(ShopPage page) {
		AbsoluteLayout layout = new AbsoluteLayout();
		layout.setData(page);
		layout.setHeight("420px");
		layout.addComponent(getPageAttributes(page), "top:20.0px;left:20.0px;right:20.0px;");
		return layout;
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

	@Override
	protected AbstractOrderedLayout getHorizontalArticleAttributes(
			CatalogArticle article) {
		AbstractOrderedLayout layout = super.getHorizontalArticleAttributes(article);
		layout.addComponentAsFirst(getAttributeComponent(CatalogArticle.getFixedAttribute("dateTo")));
		layout.addComponentAsFirst(getAttributeComponent(CatalogArticle.getFixedAttribute("dateFrom"), new Date()));

		return layout;
	}

	@Override
	protected AbstractOrderedLayout getVerticalArticleAttributes(
			CatalogArticle article) {
		AbstractOrderedLayout layout =  super.getVerticalArticleAttributes(article);

		layout.addComponentAsFirst(getAttributeComponent(CatalogArticle.getFixedAttribute("dateTo")));
		layout.addComponentAsFirst(getAttributeComponent(CatalogArticle.getFixedAttribute("dateFrom"), new Date()));

		return layout;
	}

}
