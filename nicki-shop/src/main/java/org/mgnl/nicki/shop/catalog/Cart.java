/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mgnl.nicki.shop.catalog;

import java.text.ParseException;

import org.mgnl.nicki.dynamic.objects.components.ProcessResult;
import org.mgnl.nicki.dynamic.objects.objects.Person;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.NotImplementedException;
import org.w3c.dom.Document;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.ldap.xml.XmlHelper;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author cna
 */
@SuppressWarnings("serial")
public class Cart extends DynamicObject {

    public enum STATUS {

        NEW,
        REQUESTED,
        FINISHED;

        public String getValue() {
            return this.toString().toLowerCase();
        }

        public static STATUS fromString(String str) {
            return STATUS.valueOf(str.toUpperCase());
        }
    }
	
	private Document doc = null;
    private Catalog catalog = null;
    private HashMap<String, CartEntry> cartentries = new HashMap<String, CartEntry>();
    private final String ELEM_ATTRIBUTE = "attribute";
    private final String ELEM_CARTENTRY = "entry";
    private final String ELEM_CART = "cart";
    private final String ATTR_CATALOG = "catalog";
    private final String ATTR_ACTION = "action";
    private final String ATTR_ID = "id";
    private final String ATTR_NAME = "name";

    @Override
    public void initDataModel() {
        addObjectClass("nickiCart");
        DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
        dynAttribute.setNaming();
        addAttribute(dynAttribute);

        dynAttribute = new DynamicAttribute("data", "nickiData", String.class);
        addAttribute(dynAttribute);

        dynAttribute = new DynamicAttribute("initiator", "nickiInitiator", String.class);
        dynAttribute.setForeignKey(Person.class);
        addAttribute(dynAttribute);

        dynAttribute = new DynamicAttribute("processdate", "nickiProcessDate", String.class);
        addAttribute(dynAttribute);

        dynAttribute = new DynamicAttribute("processresult", "nickiProcessResult", String.class);
        addAttribute(dynAttribute);

        dynAttribute = new DynamicAttribute("recipient", "nickiRecipient", String.class);
        dynAttribute.setForeignKey(Person.class);
        addAttribute(dynAttribute);

        dynAttribute = new DynamicAttribute("requestdate", "nickiRequestDate", String.class);
        addAttribute(dynAttribute);

        dynAttribute = new DynamicAttribute("status", "nickiStatus", String.class);
        addAttribute(dynAttribute);
    }

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
            entry = getCartEntryInstance(node);

            cartentries.put(entry.getId(), entry);
        }
    }

    public String toXml() throws DynamicObjectException {
        if (catalog == null) {
            throw new DynamicObjectException("catalog undefined");
        }

        Document doc = XmlHelper.getNewDocument();

        Element cart = doc.createElement(ELEM_CART);

        cart.setAttribute(ATTR_CATALOG, catalog.getPath());

        for (String key : cartentries.keySet()) {
			
            cart.appendChild(getCartEntryNode(doc, cartentries.get(key)));
        }

        return XmlHelper.getXml(doc);

    }
	
	

    @Override
    public void create() throws DynamicObjectException {
        put("data", toXml());
        super.create();
    }

    @Override
    public void update() throws DynamicObjectException {
        put("data", toXml());
        super.update();
    }

    public void addProcessResult(ProcessResult result) {
        throw new NotImplementedException();
    }

    public ProcessResult getProcessResult(CartEntry entry) {
        throw new NotImplementedException();
    }

    public List<ProcessResult> getProcessResults() {
        throw new NotImplementedException();
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
        return DataHelper.formatMilli.parse((String) get("processdate"));
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setStatus(STATUS status) {
        put("status", status.getValue());
    }

    public STATUS getStatus() {
        return STATUS.fromString((String) get("status"));
    }

    public void addCartEntry(CartEntry entry) {
        cartentries.put(entry.getId(), entry);

    }

    public CartEntry getCartEntry(String id) {
        return cartentries.get(id);
    }

    @SuppressWarnings("unchecked")
	public Map<String, CartEntry> getCartEntries() {
        return (Map<String, CartEntry>) cartentries.clone();
    }

    public void setRecipient(Person person) {
        put("recipient", person.getPath());
    }

    public Person getRecipient() {
        return getContext().loadObject(Person.class, (String) get("recipient"));
    }

    public void setInitiator(Person person) {
        put("initiator", person.getPath());
    }

    public Person getInitiator() {
        return getContext().loadObject(Person.class, (String) get("initiator"));
    }

    private CartEntry getCartEntryInstance(Element node) {
        if (null == node) {
            return null;
        }

        CartEntry entry = new CartEntry(
                node.getAttribute(ATTR_ID),
                CartEntry.ACTION.valueOf(node.getAttribute(ATTR_ACTION).toUpperCase()));

        for (Element element : XmlHelper.selectNodes(Element.class, node, ELEM_ATTRIBUTE)) {
            entry.addAttribute(element.getAttribute(ATTR_NAME), element.getTextContent());
        }

        return entry;
    }

    private Element getCartEntryNode(Document doc, CartEntry entry) {
      
		Element cartentry = doc.createElement(ELEM_CARTENTRY);
		
        cartentry.setAttribute(ATTR_ID, entry.getId());
        cartentry.setAttribute(ATTR_ACTION, entry.getAction().toString().toLowerCase());

        Map<String, String> attributes = entry.getAttributes();
        Element attr;

        for (String key : attributes.keySet()) {
            attr = doc.createElement(ELEM_ATTRIBUTE);
            attr.setAttribute(ATTR_NAME, key);
            attr.setTextContent(attributes.get(key));

            cartentry.appendChild(attr);
        }

        return cartentry;
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
}
