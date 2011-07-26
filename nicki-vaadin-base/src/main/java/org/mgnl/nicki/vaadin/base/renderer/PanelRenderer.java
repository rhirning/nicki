package org.mgnl.nicki.vaadin.base.renderer;

import java.util.Iterator;
import java.util.List;

import org.mgnl.nicki.dynamic.objects.objects.CatalogArticle;
import org.mgnl.nicki.dynamic.objects.objects.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.ShopViewerComponent;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;

public class PanelRenderer extends BaseShopRenderer implements ShopRenderer{

	@Override
	public Component render(ShopViewerComponent shopViewerComponent) {
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
	
	protected void showArticleAttributes(HorizontalLayout layout) {
		CatalogArticle article = (CatalogArticle) layout.getData(); 
		
		if (article.hasAttributes()) {
			for (Iterator<CatalogArticleAttribute> iterator = article.getAllAttributes().iterator(); iterator.hasNext();) {
				CatalogArticleAttribute pageAttribute = iterator.next();
				layout.addComponent(getAttributeComponent(pageAttribute));
			}
		}
	}
	


}
