package org.mgnl.nicki.vaadin.base.shop.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.dynamic.objects.objects.Resource;
import org.mgnl.nicki.dynamic.objects.objects.Role;
import org.mgnl.nicki.shop.catalog.Catalog;
import org.mgnl.nicki.shop.catalog.CatalogArticle;
import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.inventory.InventoryArticle.STATUS;

@SuppressWarnings("serial")
public class Inventory implements Serializable{
	private Person person;
	private List<String> attributeValues;

	private Map<String, InventoryArticle> articles = new HashMap<String, InventoryArticle>();

	public Inventory(Person person) {
		super();
		this.person = person;
		this.attributeValues = person.getCatalogAttributeValues();
		init();
	}

	private void init() {
		Catalog catalog = Catalog.getCatalog();
		for (Iterator<Role> iterator = person.getRoles().iterator(); iterator.hasNext();) {
			Role role = iterator.next();
			CatalogArticle article = catalog.getArticle(role);
			if (article != null) {
				List<InventoryAttribute> attributes = getAttributes(article);
				articles.put(article.getRolePath(), new InventoryArticle(article, role, attributes));
			}
		}
		for (Iterator<Resource> iterator = person.getResources().iterator(); iterator.hasNext();) {
			Resource resource = iterator.next();
			CatalogArticle article = catalog.getArticle(resource);
			if (article != null) {
				List<InventoryAttribute> attributes = getAttributes(article);
				articles.put(article.getReferencePath(), new InventoryArticle(article, resource, attributes));
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
		return articles.containsKey(article.getReferencePath());
	}

	public InventoryArticle getArticle(CatalogArticle article) {
		return articles.get(article.getReferencePath());
	}

	public void addArticle(CatalogArticle article) {
		InventoryArticle iArticle = getArticle(article);
		if (iArticle == null) {
			articles.put(article.getReferencePath(), new InventoryArticle(article));
		} else if (iArticle.getStatus() == STATUS.DELETED) {
			iArticle.reset();
		}
	}

	public void removeArticle(CatalogArticle article) {
		InventoryArticle iArticle = getArticle(article);
		if (iArticle != null) {
			if (iArticle.getStatus() == STATUS.NEW) {
				articles.remove(article.getReferencePath());
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

}
