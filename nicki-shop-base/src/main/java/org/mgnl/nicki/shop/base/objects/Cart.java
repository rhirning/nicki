
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
 *
 * @author cna
 */
@SuppressWarnings("serial")
@DynamicObject
@ObjectClass("nickiCart")
public class Cart extends BaseDynamicObject {

	public static final String ATTRIBUTE_DATA			= "data";
	public static final String ATTRIBUTE_INITIATOR		= "initiator";
	public static final String ATTRIBUTE_MANAGER		= "manager";
	public static final String ATTRIBUTE_PROCESS_RESULT	= "processResult";
	public static final String ATTRIBUTE_RECIPIENT		= "recipient";
	public static final String ATTRIBUTE_SOURCE			= "source";

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
	
    private Catalog catalog;
    private List<CartEntry> cartentries = new ArrayList<CartEntry>();
    private final static String ELEM_CARTENTRY = "entry";
    private final static String ELEM_CART = "cart";
    private final static String ATTR_CATALOG = "catalog";
    
	@DynamicAttribute(externalName = "cn", naming = true)
	public String getName() {
		return super.getName();
	}
    @DynamicAttribute(externalName="nickiCartEntry")
    private String[] cartEntry;
    
	@DynamicAttribute(externalName = "nickiData")
	public String getData() {
		return getAttribute(ATTRIBUTE_DATA);
	}

	@DynamicAttribute(externalName="nickiInitiator", foreignKey=Person.class)
	public Person getInitiator() {
		return getContext().loadObject(Person.class, getAttribute(ATTRIBUTE_INITIATOR));
	}
	
	@DynamicAttribute(externalName="nickiManager", foreignKey=Person.class)
	public Person getManager() {
		return getContext().loadObject(Person.class, getAttribute(ATTRIBUTE_MANAGER));
	}

    @DynamicAttribute(externalName="nickiProcessDate")
    private String processdate;
    
	@DynamicAttribute(externalName = "nickiProcessResult")
	public String getProcessResult() {
		return getAttribute(ATTRIBUTE_PROCESS_RESULT);
	}

    @DynamicAttribute(externalName="nickiRecipient", foreignKey=Person.class)
	public Person getRecipient() {
		return getContext().loadObject(Person.class, getAttribute(ATTRIBUTE_RECIPIENT));
	}
    @DynamicAttribute(externalName="nickiRequestDate")
    private String requestdate;
    @DynamicAttribute(externalName="nickiStatus")
    private String cartStatus;
    @DynamicAttribute(externalName="nickiStatusFlag")
    private String[] statusFlag;
    
	@DynamicAttribute(externalName = "nickiSource")
	public String getSource() {
		return getAttribute(ATTRIBUTE_SOURCE);
	}
	

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
        put("requestdate", DataHelper.getMilli(date));
    }

    public Date getRequestDate() throws ParseException {
        return DataHelper.milliFromString((String) get("requestdate"));
    }

    public void setProcessDate(Date date) {
        put("processdate", DataHelper.getMilli(date));
    }

    public Date getProcessDate() throws ParseException {
    	if (get("processdate")!= null) {
    		return DataHelper.milliFromString((String) get("processdate"));
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

    public void addCartEntry(CartEntry entry) {
        cartentries.add(entry);

    }

	public List<CartEntry> getCartEntries() {
       	return cartentries;
    }

    public void setRecipient(Person person) {
        put("recipient", person!=null?person.getPath():null);
    }

    public void setInitiator(Person person) {
        put("initiator", person!=null?person.getPath():null);
    }

    public static List<Cart> getAllCarts(NickiContext ctx) {
        return ctx.loadChildObjects(Cart.class, Config.getString("nicki.carts.basedn"), null);
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
