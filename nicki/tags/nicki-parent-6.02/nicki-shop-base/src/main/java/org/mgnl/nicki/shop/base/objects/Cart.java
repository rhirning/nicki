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
import java.util.ArrayList;

import org.mgnl.nicki.dynamic.objects.objects.Person;
import org.mgnl.nicki.shop.base.objects.CartEntry.ACTION;
import org.mgnl.nicki.shop.base.objects.CartEntry.CART_ENTRY_STATUS;

import java.util.Date;
import java.util.List;
import org.w3c.dom.Document;
import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.DynamicObject;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.objects.BaseDynamicObject;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.core.util.XmlHelper;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author cna
 */
@DynamicObject
@ObjectClass("nickiCart")
public class Cart extends BaseDynamicObject {

	private static final long serialVersionUID = 3099728171406117766L;

	public enum CART_STATUS {

		TEMP,
        NEW,
        APPROVED,
        RUNNING,
        DENIED,
        FINISHED;

        public String getValue() {
            return this.toString().toLowerCase();
        }

        public static CART_STATUS fromString(String str) {
            return CART_STATUS.valueOf(str.toUpperCase());
        }
    }
	
    private Catalog catalog = null;
    private List<CartEntry> cartentries = new ArrayList<CartEntry>();
    private final static String ELEM_CARTENTRY = "entry";
    private final static String ELEM_CART = "cart";
    private final static String ATTR_CATALOG = "catalog";
    
    @DynamicAttribute(externalName="cn", naming=true)
    private String name;
    @DynamicAttribute(externalName="nickiCartEntry")
    private String[] cartEntry;
    @DynamicAttribute(externalName="nickiData")
    private String data;
    @DynamicAttribute(externalName="nickiInitiator", foreignKey=Person.class)
    private String initiator;
    @DynamicAttribute(externalName="nickiManager", foreignKey=Person.class)
    private String manager;
    @DynamicAttribute(externalName="nickiProcessDate")
    private String processdate;
    @DynamicAttribute(externalName="nickiProcessResult")
    private String processresult;
    @DynamicAttribute(externalName="nickiRecipient", foreignKey=Person.class)
    private String recipient;
    @DynamicAttribute(externalName="nickiRequestDate")
    private String requestdate;
    @DynamicAttribute(externalName="nickiStatus")
    private String cartStatus;
    @DynamicAttribute(externalName="nickiStatusFlag")
    private String[] statusFlag;
    @DynamicAttribute(externalName="nickiSource")
    private String source;
	

    @SuppressWarnings("unchecked")
	@Override
    public void init(ContextSearchResult rs) throws DynamicObjectException {
        super.init(rs);

        try {
        	List<String> list = (List<String>) get("cartEntry");
        	if (list != null && list.size() != 0) {
        		fromStrings((List<String>) get("cartEntry"));
        	}
        	
        	fromXml((String) get("data"));
        } catch (Exception ex) {
            System.err.println("xml invalid - " + ex.getMessage());
            throw new IllegalArgumentException(ex);
        }
    }

	private void fromStrings(List<String> list) {
		if (list != null) {
			for (String string : list) {
				cartentries.add(CartEntry.fromString(string));
			}
		}
	}

	public void fromXml(String xml) throws SAXException  {

        Document doc = XmlHelper.getDocumentFromXml(xml);

        Element cart = XmlHelper.selectNode(Element.class, doc, ELEM_CART);
        catalog = getContext().loadObject(Catalog.class, cart.getAttribute(ATTR_CATALOG));
        if (catalog == null) {
            System.err.println("could not load catalog object - id is invalid: \"" + cart.getAttribute(ATTR_CATALOG) + "\"");
        }

        List<Element> entries = XmlHelper.selectNodes(Element.class, cart, ELEM_CARTENTRY);

        for (Element node : entries) {
            CartEntry tempEntry = CartEntry.fromNode(node);
            
            CartEntry entry = findEntry(tempEntry);
            if (entry != null) {
            	entry.setComment(tempEntry.getComment());
            }
        }
    }

	private CartEntry findEntry(CartEntry tempEntry) {
        if (cartentries.contains(tempEntry)) {
			for (CartEntry entry : cartentries) {
				if (entry.equals(tempEntry)) {
					return entry;
				}
			}
        }
		return null;
	}

	public String toXml() throws DynamicObjectException {
        if (catalog == null) {
            throw new DynamicObjectException("catalog undefined");
        }

        Document doc = XmlHelper.getNewDocument();

        Element cart = doc.createElement(ELEM_CART);

        cart.setAttribute(ATTR_CATALOG, catalog.getPath());

        for (CartEntry entry : cartentries) {			
            cart.appendChild(entry.getNode(doc, ELEM_CARTENTRY));
        }
		
		doc.appendChild(cart);

        return XmlHelper.getXml(doc);

    }
	
	

    @Override
    public org.mgnl.nicki.core.objects.DynamicObject create() throws DynamicObjectException {
        put("data", toXml());
    	put("cartEntry", getCartEntriesAsList());
        return super.create();
    }

    private Object getCartEntriesAsList() {
    	List<String> list = new ArrayList<String>();
    	for (CartEntry cartEntry : getCartEntries()) {
			list.add(cartEntry.asString());
		}
		return list;
	}

	@Override
    public void update() throws DynamicObjectException {
        put("data", toXml());
    	put("cartEntry", getCartEntriesAsList());
        super.update();
    }

    public void setRequestDate(Date date) {
        put("requestdate", DataHelper.formatMilli.format(date));
    }

    public Date getRequestDate() throws ParseException {
        return DataHelper.formatMilli.parse((String) get("requestdate"));
    }

    public void setProcessDate(Date date) {
        put("processdate", DataHelper.formatMilli.format(date));
    }

    public Date getProcessDate() throws ParseException {
    	if (get("processdate")!= null) {
    		return DataHelper.formatMilli.parse((String) get("processdate"));
    	} else {
    		return null;
    	}
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCartStatus(String status) {
        put("cartStatus", status);
    }

    public void setCartStatus(CART_STATUS status) {
        put("cartStatus", status.getValue());
    }
	
	public void setStatusFlag(List<String> statusFlags) {
		put("statusFlag", statusFlags);
    }
	
	@SuppressWarnings("unchecked")
	public void addStatusFlag(String statusFlag) {
		List<String> list;
		if (get("statusFlag") != null) {
			list = (List<String>) get("statusFlag");
		} else {
			list = new ArrayList<String>();
		}
		
		list.add(statusFlag);
		put("statusFlag", list);
    }

    public CART_STATUS getCartStatus() {
        return CART_STATUS.fromString((String) get("cartStatus"));
    }
    
	@SuppressWarnings("unchecked")
	public List<String> getStatusFlag() {
        return (List<String>) get("statusFlag");
    }
	
	public void setSource(String source) {
        put("source", source);
    }

    public String getSource() {
        return (String) get("source");
    }

    public void addCartEntry(CartEntry entry) {
        cartentries.add(entry);

    }

	public List<CartEntry> getCartEntries() {
       	return cartentries;
    }

    public void setRecipient(Person person) {
        put("recipient", person!=null?person.getPath():null);
    }

    public Person getRecipient() {
        return getContext().loadObject(Person.class, (String) get("recipient"));
    }

    public void setInitiator(Person person) {
        put("initiator", person!=null?person.getPath():null);
    }

    public Person getInitiator() {
        return getContext().loadObject(Person.class, (String) get("initiator"));
    }

    public static List<Cart> getAllCarts(NickiContext ctx) {
        return ctx.loadChildObjects(Cart.class, Config.getProperty("nicki.carts.basedn"), null);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[CART name=");
        sb.append(getName());
        sb.append(" catalog=");
        sb.append((catalog == null) ? "" : catalog.getPath());
        sb.append(" cartentries.length=");
        sb.append(cartentries.size());
        sb.append("]");


        return sb.toString();
    }

	public Person getManager() {
		return getContext().loadObject(Person.class, (String) get("manager"));
	}

	public void setManager(Person person) {
		put("manager", person!=null?person.getPath():null);
	}

	public void removeEntry(CartEntry cartEntry) {
		if (cartentries.contains(cartEntry)) {
			cartentries.remove(cartEntry);
		}
	}

	/*
	 * Status: FINISHED, wenn alle entries finished oder denied sind
	 * Status: 
	 */
	public void updateStatus(String permissionDn, String specifier,
			ACTION cartEntryAction, CART_ENTRY_STATUS oldCartEntryStatus,
			CART_ENTRY_STATUS newCartEntryStatus, String comment) {
		boolean finished = true;
		for (CartEntry cartEntry : cartentries) {
			if (cartEntry.match(permissionDn, specifier, cartEntryAction, oldCartEntryStatus)) {
				cartEntry.updateStatus(newCartEntryStatus, comment);
			}
			finished = finished && (cartEntry.getStatus() == CART_ENTRY_STATUS.FINISHED ||
					cartEntry.getStatus() == CART_ENTRY_STATUS.DENIED);
		}
		if (finished) {
			setCartStatus(CART_STATUS.FINISHED);
		}
	}
}
