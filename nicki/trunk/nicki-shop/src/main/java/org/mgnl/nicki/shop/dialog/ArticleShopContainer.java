package org.mgnl.nicki.shop.dialog;


import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.objects.Shop;
import org.mgnl.nicki.dynamic.objects.objects.ShopArticle;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

@SuppressWarnings("serial")
public class ArticleShopContainer implements ShopContainer{

	private String STATUS_ORDERED; 
	private String STATUS_CANCELED; 
	private String STATUS_PROVISIONED; 

    private String[] visibleColumns = new String[] {
    	PROPERTY_STATUS,
    	PROPERTY_NAME,
    	PROPERTY_PATH
    };
	
	private IndexedContainer container = new IndexedContainer();
	private DynamicObject person;
	private Shop shop;

	public ArticleShopContainer(Shop shop, DynamicObject person, String i18nBase) {
		super();
		this.shop = shop;
		this.person = person;
		STATUS_ORDERED = I18n.getText(i18nBase + ".status.ordered");
		STATUS_CANCELED = I18n.getText(i18nBase + ".status.canceled");
		STATUS_PROVISIONED = I18n.getText(i18nBase + ".status.provisioned");
	}



	public IndexedContainer getArticles() throws DynamicObjectException {
		container = new IndexedContainer();
		container.addContainerProperty(PROPERTY_STATUS, String.class, null);
		container.addContainerProperty(PROPERTY_NAME, String.class, null);
		container.addContainerProperty(PROPERTY_CATEGORY, String.class, null);

		container.addContainerProperty(PROPERTY_PATH, String.class, null);
		List<DynamicObject> loadedArticles = shop.getContext().loadObjects(shop.getPath(), "(objectClass=nickiShopArticle)");

	    if (loadedArticles != null) {
		    for (Iterator<DynamicObject> iterator = loadedArticles.iterator(); iterator.hasNext();) {
				DynamicObject p = iterator.next();
				if (p instanceof ShopArticle) {
					ShopArticle article = (ShopArticle) p;
					article.setShop(shop);
					Item item = container.addItem(article);
					item.getItemProperty(PROPERTY_NAME).setValue(article.getName());
					item.getItemProperty(PROPERTY_PATH).setValue(article.getCatalogPath());
					item.getItemProperty(PROPERTY_CATEGORY).setValue(article.get(PROPERTY_CATEGORY).toString());
					if (getPerson().hasArticle(article)) {
						item.getItemProperty(PROPERTY_STATUS).setValue(STATUS_PROVISIONED);
					}
				}
			}
	    }
		
		return container;
	}



	public void orderItem(Object target) {
		Item item = container.getItem(target);
		item.getItemProperty(PROPERTY_STATUS).setValue(STATUS_ORDERED);
	}
	
	public void cancelItem(Object target) {
		Item item = container.getItem(target);
		String oldValue = (String) item.getItemProperty(PROPERTY_STATUS).getValue();

		if (StringUtils.equals(oldValue, STATUS_ORDERED)) {
			item.getItemProperty(PROPERTY_STATUS).setValue("");
		} else if (StringUtils.equals(oldValue, STATUS_PROVISIONED)) {
			item.getItemProperty(PROPERTY_STATUS).setValue(STATUS_CANCELED);
		}
	}



	public void setCategoryFilter(List<Object> values) {
		this.container.removeAllContainerFilters();
		if (values != null && values.size() > 0) {
			for (Iterator<Object> iterator = values.iterator(); iterator.hasNext();) {
				String filterString = (String) iterator.next();
				this.container.addContainerFilter(PROPERTY_CATEGORY, filterString, true, false);
			}
		}
	}



	public Person getPerson() {
		return ((Person)person);
	}



	public String[] getVisibleColumns() {
		return visibleColumns;
	}
	

}
