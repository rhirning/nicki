package org.mgnl.nicki.vaadin.base.shop.inventory;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.dynamic.objects.objects.Resource;
import org.mgnl.nicki.dynamic.objects.objects.Role;
import org.mgnl.nicki.shop.catalog.CartEntry.ACTION;
import org.mgnl.nicki.shop.catalog.CatalogArticle;
import org.mgnl.nicki.shop.catalog.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.attributes.AttributeComponentFactory;

@SuppressWarnings("serial")
public class InventoryArticle implements Serializable{
	public enum STATUS {NONE, PROVISIONED, NEW, MODIFIED, DELETED};
	
	private STATUS originalStatus = STATUS.NONE;
	private STATUS status;
	private CatalogArticle article;
	private Map<String, InventoryAttribute> attributes = new HashMap<String, InventoryAttribute>();

	public InventoryArticle(CatalogArticle article) {
		this.article = article;
		addEmptyAttributes();
		setStatus(STATUS.NEW);
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
		setEnd(resource.getEndTime());
		setStatus(STATUS.PROVISIONED);
		originalStatus = STATUS.PROVISIONED;
	}

	public InventoryArticle(CatalogArticle article,
			Role role,
			List<InventoryAttribute> attributes) {
		this.article = article;
		addEmptyAttributes();
		if (attributes != null) {
			for (Iterator<InventoryAttribute> iterator = attributes.iterator(); iterator.hasNext();) {
				InventoryAttribute attribute = iterator.next();
				this.attributes.put(attribute.getName(), attribute);
			}
		}
		setStart(role.getStartTime(), role.getStartTime());
		setEnd(role.getEndTime(), role.getEndTime());
		setStatus(STATUS.PROVISIONED);
		originalStatus = STATUS.PROVISIONED;
	}

	public void setStart(Date start) {
		setValue(attributes.get("dateFrom"), start);
	}

	public void setStart(Date start, Date oldValue) {
		setValue(attributes.get("dateFrom"), start);
		setOldValue(attributes.get("dateFrom"), start);
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
		sb.append(" status=").append(getStatus()).append("]");
		for (Iterator<String> iterator = attributes.keySet().iterator(); iterator.hasNext();) {
			sb.append("\n").append(attributes.get(iterator.next()).toString());
		}
		return sb.toString();
	}

	public CatalogArticle getArticle() {
		return article;
	}

	public Object getValue(CatalogArticleAttribute attribute) {
		return getValue(attribute.getName());
	}

	public Object getValue(String attributeName) {
		InventoryAttribute iAttribute = attributes.get(attributeName);
		return iAttribute.getValue();
	}

	public void setValue(CatalogArticleAttribute attribute, Object value) {
		InventoryAttribute iAttribute = attributes.get(attribute.getName());
		setValue(iAttribute, value);
	}

	private void setValue(InventoryAttribute iAttribute, Object value) {
		CatalogArticleAttribute attribute = iAttribute.getAttribute();
		String stringValue = AttributeComponentFactory.getAttributeComponent(attribute.getType()).getStringValue(value);
		if (iAttribute != null) {
			if (getStatus() == STATUS.PROVISIONED && !StringUtils.equals(stringValue, iAttribute.getOldValue())) {
				setStatus(STATUS.MODIFIED);
			}
			attributes.get(attribute.getName()).setValue(stringValue);
		}
	}

	private void setOldValue(InventoryAttribute iAttribute, Object value) {
		CatalogArticleAttribute attribute = iAttribute.getAttribute();
		String stringValue = AttributeComponentFactory.getAttributeComponent(attribute.getType()).getStringValue(value);
		if (iAttribute != null) {
			if (getStatus() == STATUS.PROVISIONED && !StringUtils.equals(stringValue, iAttribute.getValue())) {
				setStatus(STATUS.MODIFIED);
			}
			attributes.get(attribute.getName()).setOldValue(stringValue);
		}
	}

	public boolean hasChanged() {
		if (getStatus() == STATUS.NEW
				|| getStatus() == STATUS.MODIFIED
				|| getStatus() == STATUS.DELETED) {
			return true;
		}
		return false;
	}

	public static ACTION getAction(STATUS status) {
		if (status == STATUS.DELETED) {
			return ACTION.DELETE;
		}
		else if (status == STATUS.MODIFIED) {
			return ACTION.MODIFY;
		}
		else if (status == STATUS.NEW) {
			return ACTION.ADD;
		}
		return null;
	}

	public Map<String, InventoryAttribute> getAttributes() {
		return attributes;
	}

	public void setEnd(Date end) {
		setValue(attributes.get("dateTo"), end);
		setStatus(STATUS.MODIFIED);
	}

	public void setEnd(Date end, Date oldEnd) {
		setValue(attributes.get("dateTo"), end);
		setOldValue(attributes.get("dateTo"), end);
		setStatus(STATUS.MODIFIED);
	}

	public Date getStart() {
		Object stored = getValue("dateFrom");
		if (stored instanceof Date) {
			return (Date) stored;
		}
		if (stored instanceof String) {
			try {
				return DataHelper.dateFromString((String) stored);
			} catch (ParseException e) {
				System.out.println(stored);
				e.printStackTrace();
			}
			System.out.println(stored);
		}
		return null;
	}
	
	public Date getEnd() {
		Object stored = getValue("dateTo");
		if (stored instanceof Date) {
			return (Date) stored;
		}
		if (stored instanceof String && StringUtils.isNotEmpty((String) stored)) {
			try {
				return DataHelper.dateFromString((String) stored);
			} catch (ParseException e) {
				System.out.println(stored);
				e.printStackTrace();
			}
			System.out.println(stored);
		}
		return null;
	}
}
