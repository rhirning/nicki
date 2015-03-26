/**
 * Copyright (c) 2003-2015 Dr. Ralf Hirning
 * All rights reserved.
 *  
 * This program is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 * 
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU Public License v3.0
 * which is available at
 * http://www.gnu.org/licenses/gpl.html
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 * 
 * Any modifications to this file must keep this entire header
 * intact.
*/
package org.mgnl.nicki.shop.base.objects;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.util.XmlHelper;
import org.mgnl.nicki.shop.base.inventory.InventoryArticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author cna
 */
public class CartEntry {
	private static final Logger LOG = LoggerFactory.getLogger(CartEntry.class);
    public static final String ATTR_ID = "id";
    public static final String ATTR_ACTION = "action";
    public static final String ATTR_SPECIFIER = "specifier";
	public static final String ATTR_NAME = "name";
    public static final String ELEM_ATTRIBUTE = "attribute";

    private String comment;
    
    public enum ACTION {

        ADD,
        MODIFY,
        DELETE
    }

	public enum CART_ENTRY_STATUS {

		NEW,
        REQUESTED,
        DENIED,
        FINISHED;

        public String getValue() {
            return this.toString().toLowerCase();
        }

        public static CART_ENTRY_STATUS fromString(String str) {
            return CART_ENTRY_STATUS.valueOf(str.toUpperCase());
        }
    }
    private InventoryArticle inventoryArticle;
    private String id;
    private ACTION action;
    private Date start = null;
    private Date end = null;
    private String specifier;
    private CART_ENTRY_STATUS status = CART_ENTRY_STATUS.NEW;
    
    private Map<String, String> attributes = new HashMap<String, String>();

    public Element getNode(Document doc, String name) {
        
		Element cartentry = doc.createElement(name);
		if (StringUtils.isNotBlank(comment)) {
			cartentry.setTextContent(comment);
		}
		
        cartentry.setAttribute(ATTR_ID, getId());
		if (specifier != null) {
	        cartentry.setAttribute(ATTR_SPECIFIER, specifier);
		}
        cartentry.setAttribute(ATTR_ACTION, getAction().toString().toLowerCase());

        Map<String, String> attributes = getAttributes();
        Element attr;

        for (String key : attributes.keySet()) {
            attr = doc.createElement(ELEM_ATTRIBUTE);
            attr.setAttribute(ATTR_NAME, key);
            attr.setTextContent(attributes.get(key));

            cartentry.appendChild(attr);
        }

        return cartentry;
    }

    public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}

	public String getSpecifier() {
		return specifier;
	}

	public static CartEntry fromNode(Element node) {
        if (null == node) {
            return null;
        }
        
        CartEntry entry = new CartEntry(
        		null,
                node.getAttribute(ATTR_ID),
                CartEntry.ACTION.valueOf(node.getAttribute(ATTR_ACTION).toUpperCase()));

        if (StringUtils.isNotEmpty(node.getAttribute(ATTR_SPECIFIER))) {
        	entry.setSpecifier(node.getAttribute(ATTR_SPECIFIER));
        }
        if (StringUtils.isNotEmpty(node.getTextContent())) {
        	entry.setComment(node.getTextContent());
        }
        for (Element element : XmlHelper.selectNodes(Element.class, node, ELEM_ATTRIBUTE)) {
            entry.addAttribute(element.getAttribute(ATTR_NAME), element.getTextContent());
        }

        return entry;
    }



    
    public ACTION getAction() {
        return action;
    }

    public String getId() {
        return id;
    }

    public CartEntry(InventoryArticle iArticle, String id, ACTION action) {
    	this.inventoryArticle = iArticle;
        this.id = id;
        this.action = action;
    }

    public CartEntry(InventoryArticle iArticle, String id, String specifier, ACTION action) {
    	this.inventoryArticle = iArticle;
        this.id = id;
        this.specifier = specifier;
        this.action = action;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public void addAttributes(Map<String, String> attributes) {
        this.attributes.putAll(attributes);
    }

    public void addAttribute(String key, String value) {
        attributes.put(key, value);
    }

    public String getAttribute(String key) {
        return attributes.get(key);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setStart(Date start) {
		this.start = start;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

    public void setSpecifier(String specifier) {
		this.specifier = specifier;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[CARTENTRY id=");
        sb.append(id);
        sb.append(", action=");
        sb.append(action.toString());
        sb.append(", attributes=");
        sb.append(attributes);
        sb.append("]");

        return sb.toString();
    }

	public InventoryArticle getInventoryArticle() {
		return inventoryArticle;
	}

	public boolean match(String permissionDn, String specifier,
			ACTION cartEntryAction, CART_ENTRY_STATUS cartEntryStatus) {
		if (!StringUtils.equals(specifier, this.specifier)) {
			return false;
		}
		if (cartEntryAction != action) {
			return false;
		}
		CatalogArticle catalogArticle = Catalog.getCatalog().getArticle(id);
		if (!StringUtils.equals(permissionDn, catalogArticle.getPermissionDn())) {
			return false;
		}
		
		return true;
	}

	public void updateStatus(CART_ENTRY_STATUS newCartEntryStatus) {
		setStatus(newCartEntryStatus);
	}

	public CART_ENTRY_STATUS getStatus() {
		return status;
	}

	public void setStatus(CART_ENTRY_STATUS status) {
		this.status = status;
	}
//	String cartEntryQualifier = catalogArticle.getId() + "#" + cartEntryAction + "#" + cartEntryStatus + "#";

	public String asString() {
		StringBuffer sb = new StringBuffer();
		sb.append(id).append("#");
		sb.append(action).append("#");
		sb.append(status);
		if (StringUtils.isNotBlank(specifier)) {
			sb.append("#");
			sb.append(specifier);
		}
		return sb.toString();
	}
	
	public static CartEntry fromString(String cartEntryString) {
		String elements[] = StringUtils.split(cartEntryString, "#", 4);
		String id = elements[0];
		String action = elements[1];
		String status = elements[2];
		String specifier = null;
		if (elements.length > 3) {
			specifier = elements[3];
		}
		CartEntry entry = new CartEntry(null, id,
                ACTION.valueOf(action));
		entry.specifier = specifier;
		entry.status = CART_ENTRY_STATUS.fromString(status);
		return entry;
	}

	public String getDisplayName() {
		String name = getCatalogArticle().getDisplayName();
		if (StringUtils.isNotBlank(specifier)) {
			name += ": " + specifier;
		}
		return name;
	}

	private CatalogArticle getCatalogArticle() {
		return Catalog.getCatalog().getArticle(id);
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CartEntry) {
			CartEntry entry = (CartEntry) obj;
			if (!StringUtils.equals(entry.specifier, this.specifier)) {
				return false;
			}
			if (!StringUtils.equals(entry.id, this.id)) {
				return false;
			}
			if (entry.action != action) {
				return false;
			}
			return true;
		}
		// TODO Auto-generated method stub
		return super.equals(obj);
	}
}
