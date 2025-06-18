
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


import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.shop.base.inventory.InventoryArticle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * The Class CartEntry.
 *
 * @author cna
 */
public class CartEntry {
    
    /** The Constant ATTR_ID. */
    public static final String ATTR_ID = "id";
    
    /** The Constant ATTR_ACTION. */
    public static final String ATTR_ACTION = "action";
    
    /** The Constant ATTR_SPECIFIER. */
    public static final String ATTR_SPECIFIER = "specifier";
	
	/** The Constant ATTR_NAME. */
	public static final String ATTR_NAME = "name";

    /** The comment. */
    private String comment;
    
    /**
     * The Enum ACTION.
     */
    public enum ACTION {

        /** The add. */
        ADD,
        
        /** The modify. */
        MODIFY,
        
        /** The delete. */
        DELETE
    }

	/**
	 * The Enum CART_ENTRY_STATUS.
	 */
	public enum CART_ENTRY_STATUS {

		/** The new. */
		NEW,
        
        /** The requested. */
        REQUESTED,
        
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
         * @return the cart entry status
         */
        public static CART_ENTRY_STATUS fromString(String str) {
            return CART_ENTRY_STATUS.valueOf(str.toUpperCase());
        }
    }
    
    /** The inventory article. */
    private InventoryArticle inventoryArticle;
    
    /** The id. */
    private String id;
    
    /** The action. */
    private ACTION action;
    
    /** The start. */
    private Date start;
    
    /** The end. */
    private Date end;
    
    /** The specifier. */
    private String specifier;
    
    /** The status. */
    private CART_ENTRY_STATUS status = CART_ENTRY_STATUS.NEW;

    /**
     * Gets the node.
     *
     * @param doc the doc
     * @param name the name
     * @return the node
     */
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

        return cartentry;
    }

    /**
     * Gets the start.
     *
     * @return the start
     */
    public Date getStart() {
		return start;
	}

	/**
	 * Gets the end.
	 *
	 * @return the end
	 */
	public Date getEnd() {
		return end;
	}

	/**
	 * Gets the specifier.
	 *
	 * @return the specifier
	 */
	public String getSpecifier() {
		return specifier;
	}

	/**
	 * From node.
	 *
	 * @param node the node
	 * @return the cart entry
	 */
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

        return entry;
    }



    
    /**
     * Gets the action.
     *
     * @return the action
     */
    public ACTION getAction() {
        return action;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Instantiates a new cart entry.
     *
     * @param iArticle the i article
     * @param id the id
     * @param action the action
     */
    public CartEntry(InventoryArticle iArticle, String id, ACTION action) {
    	this.inventoryArticle = iArticle;
        this.id = id;
        this.action = action;
    }

    /**
     * Instantiates a new cart entry.
     *
     * @param iArticle the i article
     * @param id the id
     * @param specifier the specifier
     * @param action the action
     */
    public CartEntry(InventoryArticle iArticle, String id, String specifier, ACTION action) {
    	this.inventoryArticle = iArticle;
        this.id = id;
        this.specifier = specifier;
        this.action = action;
    }

    /**
     * Sets the start.
     *
     * @param start the new start
     */
    public void setStart(Date start) {
		this.start = start;
	}

	/**
	 * Sets the end.
	 *
	 * @param end the new end
	 */
	public void setEnd(Date end) {
		this.end = end;
	}

    /**
     * Sets the specifier.
     *
     * @param specifier the new specifier
     */
    public void setSpecifier(String specifier) {
		this.specifier = specifier;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[CARTENTRY id=");
        sb.append(id);
        sb.append(", action=");
        sb.append(action.toString());
        sb.append("]");

        return sb.toString();
    }

	/**
	 * Gets the inventory article.
	 *
	 * @return the inventory article
	 */
	public InventoryArticle getInventoryArticle() {
		return inventoryArticle;
	}

	/**
	 * Match.
	 *
	 * @param permissionDn the permission dn
	 * @param specifier the specifier
	 * @param cartEntryAction the cart entry action
	 * @param cartEntryStatus the cart entry status
	 * @return true, if successful
	 */
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

	/**
	 * Update status.
	 *
	 * @param newCartEntryStatus the new cart entry status
	 * @param comment the comment
	 */
	public void updateStatus(CART_ENTRY_STATUS newCartEntryStatus, String comment) {
		setStatus(newCartEntryStatus);
		if (StringUtils.isNotBlank(comment)) {
			setComment(comment);
		}
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public CART_ENTRY_STATUS getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(CART_ENTRY_STATUS status) {
		this.status = status;
	}
//	String cartEntryQualifier = catalogArticle.getId() + "#" + cartEntryAction + "#" + cartEntryStatus + "#";

	/**
 * As string.
 *
 * @return the string
 */
public String asString() {
		StringBuilder sb = new StringBuilder();
		sb.append(id).append("#");
		sb.append(action).append("#");
		sb.append(status);
		if (StringUtils.isNotBlank(specifier)) {
			sb.append("#");
			sb.append(specifier);
		}
		return sb.toString();
	}
	
	/**
	 * From string.
	 *
	 * @param cartEntryString the cart entry string
	 * @return the cart entry
	 */
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

	/**
	 * Gets the display name.
	 *
	 * @return the display name
	 */
	public String getDisplayName() {
		String name = getCatalogArticle().getDisplayName();
		if (StringUtils.isNotBlank(specifier)) {
			name += ": " + specifier;
		}
		return name;
	}

	/**
	 * Gets the catalog article.
	 *
	 * @return the catalog article
	 */
	public CatalogArticle getCatalogArticle() {
		return Catalog.getCatalog().getArticle(id);
	}

	/**
	 * Gets the comment.
	 *
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Sets the comment.
	 *
	 * @param comment the new comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
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

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
}
