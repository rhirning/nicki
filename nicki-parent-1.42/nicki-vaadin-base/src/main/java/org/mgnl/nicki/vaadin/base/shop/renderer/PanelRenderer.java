package org.mgnl.nicki.vaadin.base.shop.renderer;

import java.util.Iterator;
import java.util.List;

import org.mgnl.nicki.shop.catalog.CatalogArticle;
import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.core.ShopViewerComponent;
import org.mgnl.nicki.vaadin.base.shop.inventory.Inventory;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;

@SuppressWarnings("serial")
public class PanelRenderer extends BaseShopRenderer implements ShopRenderer{

	@Override
	public Component render(ShopViewerComponent shopViewerComponent, Inventory inventory) {
		setInventory(inventory);
		// collect all articles
		List<CatalogArticle> articles = shopViewerComponent.getAllArticles();
		// create Panel
		Panel panel = new Panel();
		panel.setScrollable(true);
		// add articles to panel
		for (Iterator<CatalogArticle> iterator = articles.iterator(); iterator.hasNext();) {
			CatalogArticle article = (CatalogArticle) iterator.next();
			panel.addComponent(getArticleComponent(article));
		}
		return panel;
	}
	
	protected void showArticleAttributes(HorizontalLayout layout, boolean enabled) {
		CatalogArticle article = (CatalogArticle) layout.getData(); 
		
		if (article.hasAttributes()) {
			for (Iterator<CatalogArticleAttribute> iterator = article.getAllAttributes().iterator(); iterator.hasNext();) {
				CatalogArticleAttribute pageAttribute = iterator.next();
				layout.addComponent(getAttributeComponent(article, pageAttribute, enabled));
			}
		}
	}
	


}
