package org.mgnl.nicki.dynamic.objects.objects;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.i18n.I18n;

public class CatalogArticleAttribute {
	public static final String SEPARATOR = "|"; 
	private String name;
	private String key;
	private String type;
	public CatalogArticleAttribute(String name, String key, String type) {
		this.name = name;
		this.key = key;
		this.type = type;
	}
	public CatalogArticleAttribute(String attributeString) throws Exception {
		String parts[] = StringUtils.split(attributeString, SEPARATOR);
		if (parts == null || parts.length != 3) {
			throw new Exception("Invalid attribute string: " + attributeString);
		}
		this.name = parts[0];
		this.key = parts[0];
		this.type = parts[0];
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLabel() {
		return I18n.getText(key);
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getAttributeString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name).append(SEPARATOR).append(key).append(SEPARATOR).append(type);
		return sb.toString();
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getKey() {
		return key;
	}

}
