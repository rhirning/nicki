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
package org.mgnl.nicki.shop.base.objects;

import java.text.ParseException;
import java.util.ArrayList;

import org.mgnl.nicki.dynamic.objects.objects.Person;

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
        REQUESTED,
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
	

    @Override
    public void init(ContextSearchResult rs) throws DynamicObjectException {
        super.init(rs);

        try {
            fromXml((String) get("data"));
        } catch (Exception ex) {
            System.err.println("xml invalid - " + ex.getMessage());
            throw new IllegalArgumentException(ex);
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
        CartEntry entry;

        for (Element node : entries) {
            entry = CartEntry.fromNode(node);

            cartentries.add(entry);
        }
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
        return super.create();
    }

    @Override
    public void update() throws DynamicObjectException {
        put("data", toXml());
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
}
