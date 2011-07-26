package org.mgnl.nicki.vaadin.base.renderer;

import java.util.Iterator;
import java.util.List;

import org.mgnl.nicki.dynamic.objects.objects.CatalogArticle;
import org.mgnl.nicki.dynamic.objects.objects.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.ShopViewerComponent;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;

public class TableRenderer extends BaseShopRenderer implements ShopRenderer{
	
	private Table table;

	@SuppressWarnings("serial")
	@Override
	public Component render(ShopViewerComponent shopViewerComponent) {
		// collect all articles
		List<CatalogArticle> articles = shopViewerComponent.getAllArticles();
		// create Table
		table = new Table();
		table.setWidth("100%");
		table.addContainerProperty("checkbox", CheckBox.class, "");
		table.setColumnWidth("checkbox", 20);
		table.setColumnHeader("checkbox", "");
		table.addContainerProperty("title", String.class, "");
		table.setColumnWidth("title", 200);
		table.addContainerProperty("attributes", HorizontalLayout.class, "");
		// add articles to table
		for (Iterator<CatalogArticle> iterator = articles.iterator(); iterator.hasNext();) {
			CatalogArticle article = (CatalogArticle) iterator.next();
			CheckBox checkBox = new CheckBox();
			checkBox.setData(article);
			checkBox.setImmediate(true);
			checkBox.setWidth("-1px");
			checkBox.addListener(new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					  CatalogArticle article = (CatalogArticle) event.getButton().getData();
					  Item item = table.getItem(article);
					  boolean enabled = event.getButton().booleanValue();
					  if (enabled) {
						  item.getItemProperty("attributes").setValue(getArticleAttributes(article));
//						  showArticleAttributes(parent);
					  } else {
						  item.getItemProperty("attributes").setValue(null);
//						  removeExcept(parent, event.getButton());
					  }
				}
			});
			Item item = table.addItem(article);
			item.getItemProperty("title").setValue(article.getDisplayName());
			item.getItemProperty("checkbox").setValue(checkBox);
//			item.getItemProperty("attributes").setValue(getArticleAttributes(article));
		}
		return table;
	}
	


	protected Component getArticleAttributes(CatalogArticle article) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);
		layout.setHeight("40px");
		if (article.hasAttributes()) {
			for (Iterator<CatalogArticleAttribute> iterator = article.getAllAttributes().iterator(); iterator.hasNext();) {
				CatalogArticleAttribute pageAttribute = iterator.next();
				layout.addComponent(getAttributeComponent(pageAttribute));
			}
		}
		return layout;
	}
	


}
