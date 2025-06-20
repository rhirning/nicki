
package org.mgnl.nicki.shop.base.objects;

/*-
 * #%L
 * nicki-shop-base
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


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
 * The Class Cart.
 *
 * @author cna
 */
@SuppressWarnings("serial")
@DynamicObject
@ObjectClass("nickiCart")
public class Cart extends BaseDynamicObject {

	/** The Constant ATTRIBUTE_DATA. */
	public static final String ATTRIBUTE_DATA			= "data";
	
	/** The Constant ATTRIBUTE_INITIATOR. */
	public static final String ATTRIBUTE_INITIATOR		= "initiator";
	
	/** The Constant ATTRIBUTE_MANAGER. */
	public static final String ATTRIBUTE_MANAGER		= "manager";
	
	/** The Constant ATTRIBUTE_PROCESS_RESULT. */
	public static final String ATTRIBUTE_PROCESS_RESULT	= "processResult";
	
	/** The Constant ATTRIBUTE_RECIPIENT. */
	public static final String ATTRIBUTE_RECIPIENT		= "recipient";
	
	/** The Constant ATTRIBUTE_SOURCE. */
	public static final String ATTRIBUTE_SOURCE			= "source";

	/**
	 * The Enum CART_STATUS.
	 */
	public enum CART_STATUS {

		/** The temp. */
		TEMP,
        
        /** The new. */
        NEW,
        
        /** The approved. */
        APPROVED,
        
        /** The running. */
        RUNNING,
        
        /** The denied. */
        DENIED,
        
        /** The finished. */
        FINISHED;

        /**
         * Gets the value.
         *
         * @return the value
         */
        public String getValue() {
            return this.toString().toLowerCase();
        }

        /**
         * From string.
         *
         * @param str the str
         * @return the cart status
         */
        public static CART_STATUS fromString(String str) {
            return CART_STATUS.valueOf(str.toUpperCase());
        }
    }
	
    /** The catalog. */
    private Catalog catalog;
    
    /** The cartentries. */
    private List<CartEntry> cartentries = new ArrayList<CartEntry>();
    
    /** The Constant ELEM_CARTENTRY. */
    private final static String ELEM_CARTENTRY = "entry";
    
    /** The Constant ELEM_CART. */
    private final static String ELEM_CART = "cart";
    
    /** The Constant ATTR_CATALOG. */
    private final static String ATTR_CATALOG = "catalog";
    
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@DynamicAttribute(externalName = "cn", naming = true)
	public String getName() {
		return super.getName();
	}
    
    /** The cart entry. */
    @DynamicAttribute(externalName="nickiCartEntry")
    private String[] cartEntry;
    
	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	@DynamicAttribute(externalName = "nickiData")
	public String getData() {
		return getAttribute(ATTRIBUTE_DATA);
	}

	/**
	 * Gets the initiator.
	 *
	 * @return the initiator
	 */
	@DynamicAttribute(externalName="nickiInitiator", foreignKey=Person.class)
	public Person getInitiator() {
		return getContext().loadObject(Person.class, getAttribute(ATTRIBUTE_INITIATOR));
	}
	
	/**
	 * Gets the manager.
	 *
	 * @return the manager
	 */
	@DynamicAttribute(externalName="nickiManager", foreignKey=Person.class)
	public Person getManager() {
		return getContext().loadObject(Person.class, getAttribute(ATTRIBUTE_MANAGER));
	}

    /** The processdate. */
    @DynamicAttribute(externalName="nickiProcessDate")
    private String processdate;
    
	/**
	 * Gets the process result.
	 *
	 * @return the process result
	 */
	@DynamicAttribute(externalName = "nickiProcessResult")
	public String getProcessResult() {
		return getAttribute(ATTRIBUTE_PROCESS_RESULT);
	}

    /**
     * Gets the recipient.
     *
     * @return the recipient
     */
    @DynamicAttribute(externalName="nickiRecipient", foreignKey=Person.class)
	public Person getRecipient() {
		return getContext().loadObject(Person.class, getAttribute(ATTRIBUTE_RECIPIENT));
	}
    
    /** The requestdate. */
    @DynamicAttribute(externalName="nickiRequestDate")
    private String requestdate;
    
    /** The cart status. */
    @DynamicAttribute(externalName="nickiStatus")
    private String cartStatus;
    
    /** The status flag. */
    @DynamicAttribute(externalName="nickiStatusFlag")
    private String[] statusFlag;
    
	/**
	 * Gets the source.
	 *
	 * @return the source
	 */
	@DynamicAttribute(externalName = "nickiSource")
	public String getSource() {
		return getAttribute(ATTRIBUTE_SOURCE);
	}
	

    /**
     * Inits the.
     *
     * @param rs the rs
     * @throws DynamicObjectException the dynamic object exception
     */
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

	/**
	 * From strings.
	 *
	 * @param list the list
	 */
	private void fromStrings(List<String> list) {
		if (list != null) {
			for (String string : list) {
				cartentries.add(CartEntry.fromString(string));
			}
		}
	}

	/**
	 * From xml.
	 *
	 * @param xml the xml
	 * @throws SAXException the SAX exception
	 */
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

	/**
	 * Find entry.
	 *
	 * @param tempEntry the temp entry
	 * @return the cart entry
	 */
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

	/**
	 * To xml.
	 *
	 * @return the string
	 * @throws DynamicObjectException the dynamic object exception
	 */
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
	
	

    /**
     * Creates the.
     *
     * @return the org.mgnl.nicki.core.objects. dynamic object
     * @throws DynamicObjectException the dynamic object exception
     */
    @Override
    public org.mgnl.nicki.core.objects.DynamicObject create() throws DynamicObjectException {
        put("data", toXml());
    	put("cartEntry", getCartEntriesAsList());
        return super.create();
    }

    /**
     * Gets the cart entries as list.
     *
     * @return the cart entries as list
     */
    private Object getCartEntriesAsList() {
    	List<String> list = new ArrayList<String>();
    	for (CartEntry cartEntry : getCartEntries()) {
			list.add(cartEntry.asString());
		}
		return list;
	}

	/**
	 * Update.
	 *
	 * @throws DynamicObjectException the dynamic object exception
	 */
	@Override
    public void update() throws DynamicObjectException {
        put("data", toXml());
    	put("cartEntry", getCartEntriesAsList());
        super.update();
    }

    /**
     * Sets the request date.
     *
     * @param date the new request date
     */
    public void setRequestDate(Date date) {
        put("requestdate", DataHelper.getMilli(date));
    }

    /**
     * Gets the request date.
     *
     * @return the request date
     * @throws ParseException the parse exception
     */
    public Date getRequestDate() throws ParseException {
        return DataHelper.milliFromString((String) get("requestdate"));
    }

    /**
     * Sets the process date.
     *
     * @param date the new process date
     */
    public void setProcessDate(Date date) {
        put("processdate", DataHelper.getMilli(date));
    }

    /**
     * Gets the process date.
     *
     * @return the process date
     * @throws ParseException the parse exception
     */
    public Date getProcessDate() throws ParseException {
    	if (get("processdate")!= null) {
    		return DataHelper.milliFromString((String) get("processdate"));
    	} else {
    		return null;
    	}
    }

    /**
     * Sets the catalog.
     *
     * @param catalog the new catalog
     */
    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    /**
     * Gets the catalog.
     *
     * @return the catalog
     */
    public Catalog getCatalog() {
        return catalog;
    }

    /**
     * Sets the cart status.
     *
     * @param status the new cart status
     */
    public void setCartStatus(String status) {
        put("cartStatus", status);
    }

    /**
     * Sets the cart status.
     *
     * @param status the new cart status
     */
    public void setCartStatus(CART_STATUS status) {
        put("cartStatus", status.getValue());
    }
	
	/**
	 * Sets the status flag.
	 *
	 * @param statusFlags the new status flag
	 */
	public void setStatusFlag(List<String> statusFlags) {
		put("statusFlag", statusFlags);
    }
	
	/**
	 * Adds the status flag.
	 *
	 * @param statusFlag the status flag
	 */
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

    /**
     * Gets the cart status.
     *
     * @return the cart status
     */
    public CART_STATUS getCartStatus() {
        return CART_STATUS.fromString((String) get("cartStatus"));
    }
    
	/**
	 * Gets the status flag.
	 *
	 * @return the status flag
	 */
	@SuppressWarnings("unchecked")
	public List<String> getStatusFlag() {
        return (List<String>) get("statusFlag");
    }
	
	/**
	 * Sets the source.
	 *
	 * @param source the new source
	 */
	public void setSource(String source) {
        put("source", source);
    }

    /**
     * Adds the cart entry.
     *
     * @param entry the entry
     */
    public void addCartEntry(CartEntry entry) {
        cartentries.add(entry);

    }

	/**
	 * Gets the cart entries.
	 *
	 * @return the cart entries
	 */
	public List<CartEntry> getCartEntries() {
       	return cartentries;
    }

    /**
     * Sets the recipient.
     *
     * @param person the new recipient
     */
    public void setRecipient(Person person) {
        put("recipient", person!=null?person.getPath():null);
    }

    /**
     * Sets the initiator.
     *
     * @param person the new initiator
     */
    public void setInitiator(Person person) {
        put("initiator", person!=null?person.getPath():null);
    }

    /**
     * Gets the all carts.
     *
     * @param ctx the ctx
     * @return the all carts
     */
    public static List<Cart> getAllCarts(NickiContext ctx) {
        return ctx.loadChildObjects(Cart.class, Config.getString("nicki.carts.basedn"), null);
    }

    /**
     * To string.
     *
     * @return the string
     */
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

	/**
	 * Sets the manager.
	 *
	 * @param person the new manager
	 */
	public void setManager(Person person) {
		put("manager", person!=null?person.getPath():null);
	}

	/**
	 * Removes the entry.
	 *
	 * @param cartEntry the cart entry
	 */
	public void removeEntry(CartEntry cartEntry) {
		if (cartentries.contains(cartEntry)) {
			cartentries.remove(cartEntry);
		}
	}

	/**
	 * Update status.
	 *
	 * @param permissionDn the permission dn
	 * @param specifier the specifier
	 * @param cartEntryAction the cart entry action
	 * @param oldCartEntryStatus the old cart entry status
	 * @param newCartEntryStatus the new cart entry status
	 * @param comment the comment
	 */
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
