/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.shop.base.objects;

import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.shop.base.inventory.InventoryArticle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author cna
 */
public class CartEntry {
    public static final String ATTR_ID = "id";
    public static final String ATTR_ACTION = "action";
    public static final String ATTR_SPECIFIER = "specifier";
	public static final String ATTR_NAME = "name";

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
    private Date start;
    private Date end;
    private String specifier;
    private CART_ENTRY_STATUS status = CART_ENTRY_STATUS.NEW;

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

	public void updateStatus(CART_ENTRY_STATUS newCartEntryStatus, String comment) {
		setStatus(newCartEntryStatus);
		if (StringUtils.isNotBlank(comment)) {
			setComment(comment);
		}
	}

	public CART_ENTRY_STATUS getStatus() {
		return status;
	}

	public void setStatus(CART_ENTRY_STATUS status) {
		this.status = status;
	}
//	String cartEntryQualifier = catalogArticle.getId() + "#" + cartEntryAction + "#" + cartEntryStatus + "#";

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

	public CatalogArticle getCatalogArticle() {
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

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
}
