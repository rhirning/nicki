/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.dynamic.objects.shop;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.ldap.xml.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author cna
 */
public class CartEntry {
    public static final String ATTR_ID = "id";
    public static final String ATTR_ACTION = "action";
    public static final String ATTR_START = "start";
    public static final String ATTR_END = "end";
    public static final String ATTR_SPECIFIER = "specifier";
	public static final String ATTR_NAME = "name";
    public static final String ELEM_ATTRIBUTE = "attribute";

    public enum ACTION {

        ADD,
        MODIFY,
        DELETE
    }
    private String id;
    private ACTION action;
    private Date start = null;
    private Date end = null;
    private String specifier;
    
    private Map<String, String> attributes = new HashMap<String, String>();

    public Element getNode(Document doc, String name) {
        
		Element cartentry = doc.createElement(name);
		
		if (start != null) {
	        cartentry.setAttribute(ATTR_START, DataHelper.getDay(start));
		}
		if (end != null) {
	        cartentry.setAttribute(ATTR_START, DataHelper.getDay(end));
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

    public static CartEntry fromNode(Element node) {
        if (null == node) {
            return null;
        }
        
        CartEntry entry = new CartEntry(
                node.getAttribute(ATTR_ID),
                CartEntry.ACTION.valueOf(node.getAttribute(ATTR_ACTION).toUpperCase()));

        if (StringUtils.isNotEmpty(node.getAttribute(ATTR_SPECIFIER))) {
        	entry.setSpecifier(node.getAttribute(ATTR_SPECIFIER));
        }
        if (StringUtils.isNotEmpty(node.getAttribute(ATTR_START))) {
        	try {
				entry.setStart(DataHelper.dateFromString(node.getAttribute(ATTR_START)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
        }
        if (StringUtils.isNotEmpty(node.getAttribute(ATTR_END))) {
        	try {
				entry.setEnd(DataHelper.dateFromString(node.getAttribute(ATTR_END)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
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

    public CartEntry(String id, ACTION action) {
        this.id = id;
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
}
