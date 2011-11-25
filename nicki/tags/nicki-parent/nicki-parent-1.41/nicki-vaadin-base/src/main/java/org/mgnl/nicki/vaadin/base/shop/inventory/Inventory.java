package org.mgnl.nicki.vaadin.base.shop.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.objects.Resource;
import org.mgnl.nicki.dynamic.objects.objects.Role;
import org.mgnl.nicki.ldap.data.InstantiateDynamicObjectException;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.shop.catalog.Cart;
import org.mgnl.nicki.shop.catalog.CartEntry;
import org.mgnl.nicki.shop.catalog.Catalog;
import org.mgnl.nicki.shop.catalog.CatalogArticle;
import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.inventory.InventoryArticle.STATUS;

@SuppressWarnings("serial")
public class Inventory implements Serializable{
	private Person user;
	private Person person;
	private List<String> attributeValues;

	private Map<String, InventoryArticle> articles = new HashMap<String, InventoryArticle>();

	public Inventory(Person user, Person person) {
		super();
		this.setUser(user);
		this.person = person;
		this.attributeValues = person.getCatalogAttributeValues();
		init();
	}

	private void init() {
		Catalog catalog = Catalog.getCatalog();
		for (Iterator<Role> iterator = person.getRoles().iterator(); iterator.hasNext();) {
			Role role = iterator.next();
			List<CatalogArticle> catalogArticles = catalog.getArticles(role);
			for (CatalogArticle catalogArticle : catalogArticles) {
				List<InventoryAttribute> attributes = getAttributes(catalogArticle);
				articles.put(catalogArticle.getPath(), new InventoryArticle(catalogArticle, role, attributes));
			}
		}
		for (Iterator<Resource> iterator = person.getResources().iterator(); iterator.hasNext();) {
			Resource resource = iterator.next();
			List<CatalogArticle> catalogArticles = catalog.getArticles(resource);
			for (CatalogArticle catalogArticle : catalogArticles) {
				List<InventoryAttribute> attributes = getAttributes(catalogArticle);
				articles.put(catalogArticle.getPath(), new InventoryArticle(catalogArticle, resource, attributes));
			}
		}
	}

	private List<InventoryAttribute> getAttributes(CatalogArticle article) {
		List<InventoryAttribute> attributes = new ArrayList<InventoryAttribute>();
		for (Iterator<CatalogArticleAttribute> iterator = article.getAllAttributes().iterator(); iterator.hasNext();) {
			CatalogArticleAttribute attribute = iterator.next();
			String value = getCatalogAttributeValue(article, attribute);
			attributes.add(new InventoryAttribute(article, attribute, value));
		}
		return attributes;
	}

	private String getCatalogAttributeValue(CatalogArticle article, CatalogArticleAttribute attribute) {
		String attributeId = article.getAttributeId(attribute.getName());
		for (Iterator<String> iterator = this.attributeValues.iterator(); iterator.hasNext();) {
			String entry = iterator.next();
			if (StringUtils.startsWith(entry, attributeId + "=")) {
				return StringUtils.substringAfter(entry, attributeId + "=");
			}
			
		}
		return null;
	}

	public boolean hasArticle(CatalogArticle article) {
		return articles.containsKey(article.getPath());
	}

	public InventoryArticle getArticle(CatalogArticle article) {
		return articles.get(article.getPath());
	}

	public void addArticle(CatalogArticle article) {
		InventoryArticle iArticle = getArticle(article);
		if (iArticle == null) {
			articles.put(article.getPath(), new InventoryArticle(article));
		} else if (iArticle.getStatus() == STATUS.DELETED) {
			iArticle.reset();
		}
	}

	public void removeArticle(CatalogArticle article) {
		InventoryArticle iArticle = getArticle(article);
		if (iArticle != null) {
			if (iArticle.getStatus() == STATUS.NEW) {
				articles.remove(article.getPath());
			} else {
				iArticle.setStatus(STATUS.DELETED);
			}
		}
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Inventory for ").append(person.getDisplayName()).append("\n");
		for (Iterator<String> iterator = articles.keySet().iterator(); iterator.hasNext();) {
			String key= iterator.next();
			sb.append(articles.get(key).toString()).append("\n");
		}
		return sb.toString();
	}
	
	public Cart save() throws InstantiateDynamicObjectException, DynamicObjectException {
		System.out.println(toString());
		if (hasChanged()) {
			Cart cart = person.getContext().getObjectFactory().getDynamicObject(Cart.class);
			cart.initNew(Config.getProperty("nicki.carts.basedn"), Long.toString(new Date().getTime()));
			cart.setInitiator(user);
			cart.setRecipient(person);
			cart.setRequestDate(new Date());
			cart.setStatus(Cart.STATUS.REQUESTED);
			cart.setCatalog(Catalog.getCatalog());
			for (Iterator<InventoryArticle> iterator = articles.values().iterator(); iterator.hasNext();) {
				InventoryArticle iArticle = iterator.next();
				if (iArticle.hasChanged()) {
					CartEntry entry = new CartEntry(iArticle.getArticle().getCatalogPath(),
											InventoryArticle.getAction(iArticle.getStatus()));
					for (Iterator<InventoryAttribute> iterator2 = iArticle.getAttributes().values().iterator();
							iterator2.hasNext();) {
						InventoryAttribute iAttribute = iterator2.next();
						entry.addAttribute(iAttribute.getName(), iAttribute.getValue());
					}
					cart.addCartEntry(entry);
				}
			}
			cart.create();
			return cart;
		}
		return null;
	}

	public boolean hasChanged() {
		for (Iterator<InventoryArticle> iterator = articles.values().iterator(); iterator.hasNext();) {
			if (iterator.next().hasChanged()) {
				return true;
			}
		}
		return false;
	}

	public void setUser(Person user) {
		this.user = user;
	}

	public Person getUser() {
		return user;
	}

	public Person getPerson() {
		return person;
	}

}
