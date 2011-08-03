package org.mgnl.nicki.vaadin.base.shop.inventory;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mgnl.nicki.dynamic.objects.objects.Resource;
import org.mgnl.nicki.dynamic.objects.objects.Role;
import org.mgnl.nicki.shop.catalog.CatalogArticle;
import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.attributes.AttributeComponentFactory;

@SuppressWarnings("serial")
public class InventoryArticle implements Serializable{
	public enum STATUS {NONE, PROVISIONED, NEW, MODIFIED, DELETED};
	
	private Date start;
	private STATUS originalStatus = STATUS.NONE;
	private STATUS status;
	private CatalogArticle article;
	private Map<String, InventoryAttribute> attributes = new HashMap<String, InventoryAttribute>();

	public InventoryArticle(CatalogArticle article) {
		this.article = article;
		setStatus(STATUS.NEW);
		addEmptyAttributes();
	}

	private void addEmptyAttributes() {
		for (Iterator<CatalogArticleAttribute> iterator = CatalogArticle.getFixedAttributes().iterator(); iterator.hasNext();) {
			CatalogArticleAttribute attribute =  iterator.next();
			this.attributes.put(attribute.getName(), new InventoryAttribute(article, attribute, ""));
		}
		for (Iterator<CatalogArticleAttribute> iterator = this.article.getAllAttributes().iterator(); iterator.hasNext();) {
			CatalogArticleAttribute attribute =  iterator.next();
			this.attributes.put(attribute.getName(), new InventoryAttribute(article, attribute, ""));
		}
	}

	public InventoryArticle(CatalogArticle article,
			Resource resource,
			List<InventoryAttribute> attributes) {
		this.article = article;
		addEmptyAttributes();
		if (attributes != null) {
			for (Iterator<InventoryAttribute> iterator = attributes.iterator(); iterator.hasNext();) {
				InventoryAttribute attribute = iterator.next();
				this.attributes.put(attribute.getName(), attribute);
			}
		}
		setStart(resource.getStartTime());
		setStatus(STATUS.PROVISIONED);
		originalStatus = STATUS.PROVISIONED;
	}

	public InventoryArticle(CatalogArticle article,
			Role role,
			List<InventoryAttribute> attributes) {
		setStart(role.getStartTime());
		setStatus(STATUS.PROVISIONED);
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getStart() {
		return start;
	}

	public void setStatus(STATUS status) {
		this.status = status;
	}

	public STATUS getStatus() {
		return status;
	}

	public STATUS getOriginalStatus() {
		return originalStatus;
	}

	public void reset() {
		// TODO reset attributes
		setStatus(STATUS.PROVISIONED);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[Article path=").append(article.getPath());
		sb.append(" target=").append(article.getReferencePath());
		sb.append(" status=").append(getStatus());
		sb.append(" start=").append(getStart()).append("]");
		for (Iterator<String> iterator = attributes.keySet().iterator(); iterator.hasNext();) {
			sb.append("\n").append(attributes.get(iterator.next()).toString());
		}
		return sb.toString();
	}

	public CatalogArticle getArticle() {
		return article;
	}

	public void setValue(CatalogArticleAttribute attribute, Object value) {
		String stringValue = AttributeComponentFactory.getAttributeComponent(attribute.getType()).getStringValue(value);
		attributes.get(attribute.getName()).setValue(stringValue);
	}
}
