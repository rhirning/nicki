/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mgnl.nicki.dynamic.objects.objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.mgnl.nicki.dynamic.objects.components.ProcessResult;
import org.mgnl.nicki.dynamic.objects.components.CartEntry;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.NotImplementedException;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.xml.XmlHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author cna
 */
public class Cart extends DynamicObject {

    public enum STATUS {

        NEW,
        REQUESTED,
        FINISHED;

        @Override
        public String toString() {
            return this.toString().toLowerCase();
        }

        public static STATUS fromString(String str) {
            return STATUS.valueOf(str.toUpperCase());
        }
    }
    
    private XmlHelper xmlhelper = new XmlHelper();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-hh-mm-ss-SSS");

    private String CART_XML_BASE = "<cart catalog=\"\"></cart>";
    
    private final String ELEM_ATTRIBUTE = "attribute";
    private final String ELEM_CARTENTRY = "entry";
    private final String ELEM_CART = "cart";
    private final String ATTR_CATALOG = "catalog";
    private final String ATTR_ACTION = "action";
    private final String ATTR_ID = "id";
    private final String ATTR_NAME = "name";
    
    public Cart() {
        super();
        put("data", CART_XML_BASE);
    }

    @Override
    public void initDataModel() {
        addObjectClass("nickiCart");
        DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
        dynAttribute.setNaming();
        addAttribute(dynAttribute);

        dynAttribute = new DynamicAttribute("data", "nickiData", String.class);
        addAttribute(dynAttribute);

        dynAttribute = new DynamicAttribute("initiator", "nickiInitiator", String.class);
        dynAttribute.setForeignKey();
        addAttribute(dynAttribute);

        dynAttribute = new DynamicAttribute("processdate", "nickiProcessDate", String.class);
        addAttribute(dynAttribute);

        dynAttribute = new DynamicAttribute("processresult", "nickiProcessResult", String.class);
        addAttribute(dynAttribute);

        dynAttribute = new DynamicAttribute("recipient", "nickiRecipient", String.class);
        dynAttribute.setForeignKey();
        addAttribute(dynAttribute);

        dynAttribute = new DynamicAttribute("requestdate", "nickiRequestDate", String.class);
        addAttribute(dynAttribute);

        dynAttribute = new DynamicAttribute("status", "nickiStatus", String.class);
        addAttribute(dynAttribute);
    }

    @Override
    public void init(NickiContext context, ContextSearchResult rs) {
        super.init(context, rs);
        
        try {
            xmlhelper.useXml((String) get("data"));
        } catch (SAXException ex) {
            try {
                System.err.println("xml parse exception - " + ex.getMessage());
                xmlhelper.useXml(CART_XML_BASE);
            } catch (SAXException e) {
            }
        }
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
        put("requestdate", sdf.format(date));
    }

    public Date getRequestDate() throws ParseException {
        return sdf.parse((String) get("requestdate"));
    }

    public void setProcessDate(Date date) {
        put("processdate", sdf.format(date));
    }

    public Date getProcessDate() throws ParseException {
        return sdf.parse((String) get("processdate"));
    }

    public void setCatalog(Catalog catalog) {
        xmlhelper.setAttribute("/" + ELEM_CART, ATTR_CATALOG, catalog.getName());
        put("data", xmlhelper.getXml());
    }

    public Catalog getCatalog() {
        String path = "cn=" + xmlhelper.getAttribute("/" + ELEM_CART + "/@" + ATTR_CATALOG) + "," + Config.getProperty("nicki.shops.basedn");
        return getContext().loadObject(Catalog.class, path);
    }

    public void setStatus(STATUS status) {
        put("status", status.toString());
    }

    public STATUS getStatus() {
        return STATUS.fromString((String) get("status"));
    }

    public void addCartEntry(CartEntry entry) {
        
        xmlhelper.getDocument().getFirstChild().appendChild(getCartEntryNode(entry));
        put("data", xmlhelper.getXml());

    }

    public CartEntry getCartEntry(String path) {
        String xpath = "/cart/entry[@id=\"" + path + "\"]";
        Element node = xmlhelper.selectNode(Element.class, xpath);

        CartEntry entry = getCartEntryInstance(node);

        return entry;
    }

    public List<CartEntry> getCartEntries() {
        List<Element> list = xmlhelper.selectNodes(Element.class, "/cart/entry");
        List<CartEntry> entrylist = new ArrayList<CartEntry>();

        for (Element node : list) {
            entrylist.add(getCartEntryInstance(node));
        }

        return entrylist;
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
        
        String id;
        CartEntry.ACTION action;
        
        try {
            id = xmlhelper.selectNode(Attr.class, node, "@id").getValue();
            action = CartEntry.ACTION.valueOf(
                xmlhelper.selectNode(Attr.class, node, "@action").getValue().toUpperCase());
        } catch (Exception e) {
            System.err.println("node cannot be interpreted as cartentry");
            return null;
        }
        
        CartEntry entry = new CartEntry(id, action);

        for (Element element : xmlhelper.selectNodes(Element.class, node, "attribute")) {
            entry.addAttribute(element.getAttribute("name"), element.getTextContent());
        }

        return entry;
    }

    private Element getCartEntryNode(CartEntry entry) {
        Element cartentry = xmlhelper.getDocument().createElement("entry");

        cartentry.setAttribute("id", entry.getId());
        cartentry.setAttribute("action", entry.getAction().toString().toLowerCase());

        Map<String, String> attributes = entry.getAttributes();
        Element attr;

        for (String key : attributes.keySet()) {
            attr = xmlhelper.getDocument().createElement("attribute");
            attr.setAttribute("name", key);
            attr.setTextContent(attributes.get(key));

            cartentry.appendChild(attr);
        }

        return cartentry;
    }

}
