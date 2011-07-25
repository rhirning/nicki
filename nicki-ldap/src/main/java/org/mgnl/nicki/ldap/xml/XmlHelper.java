/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mgnl.nicki.ldap.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

/**
 *
 * @author cna
 */
public class XmlHelper {

    private Document doc;
    private XPath navigator = null;
    private DocumentBuilder db = null;

    public XmlHelper() {
        navigator = XPathFactory.newInstance().newXPath();
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
        
        doc = db.newDocument();

    }
    
    public Document getDocument() {
        return doc;
    }
    
    public void query(XpathQueryHandler handler) {
        
        Node ctx;
        if (handler.getContextNode() == null) {
            ctx = doc;
        } else {
            ctx = handler.getContextNode();
        }
        
        handler.handle(selectNodes(handler.getResultType(), ctx, handler.getXpath()));
    }

    public Document useXml(String xml) throws SAXException {
        Document temp = getDocumentFromXml(xml);

        doc = temp;
        
        return doc;
    }

    public void setAttribute(String xpath, String attrname, String attrvalue) {
        selectNode(Element.class, doc, xpath).setAttribute(attrname, attrvalue);
    }
    
    public String getAttribute(String xpath) {
        return selectNode(Attr.class, doc, xpath).getValue();
    }
    
    
    public <T extends Node> T selectNode(Class<T> clazz, String xpath) {
        return selectNode(clazz, doc, xpath);
    }
    
    public <T extends Node> List<T> selectNodes(Class<T> clazz, String xpath) {
        return selectNodes(clazz, doc, xpath);
    }
    
    public <T extends Node> List<T> selectNodes(Class<T> clazz, Node ctx, String xpath) {
        //TODO XPATH parsen und $ELEM_* && $ATTR_* ersetzen durch Konstanten
        NodeList nodes;
        List<T> list = new ArrayList<T>();

        try {
            XPathExpression expr = navigator.compile(xpath);
            nodes = (NodeList) expr.evaluate(ctx, XPathConstants.NODESET);
        } catch (XPathExpressionException ex) {
            System.err.println("corrupted xpath expression: " + xpath + " - " + ex.getMessage());
            return null;
        }

        for (int i = 0; i < nodes.getLength(); i++) {
            if (clazz.isInstance(nodes.item(i))) {
                list.add((T) nodes.item(i));
            }
        }

        return list;
    }

    public <T extends Node> T selectNode(Class<T> clazz, Node ctx, String xpath) {
        //TODO XPATH parsen und $ELEM_* && $ATTR_* ersetzen durch Konstanten
        try {
            XPathExpression expr = navigator.compile(xpath);
            Object node = expr.evaluate(ctx, XPathConstants.NODE);
            if (clazz.isInstance(node)) {
                return (T) node;
            }
            return null;
        } catch (XPathExpressionException ex) {
            System.err.println("corrupted xpath expression: " + xpath + " - " + ex.getMessage());
            return null;
        }
    }

    protected Document getDocumentFromXml(String xml) throws SAXException {
        if (null == xml) {
            xml = "";
        }

        InputStream is = new ByteArrayInputStream(xml.getBytes());
        
        Document document;
        try {
            document = db.parse(is);
        } catch (IOException ioe) {
            return null;
        } catch (IllegalArgumentException iae) {
            return null;
        }

        return document;

    }

    public String getXml() {
        return getXmlFromNode(doc);
    }
    
    public String getXmlFromNode(Node node) {
        DOMImplementationLS impl = (DOMImplementationLS) doc.getImplementation();
        LSSerializer serializer = impl.createLSSerializer();
        String xml = serializer.writeToString(node);
        return StringUtils.substringAfter(xml, "\n");
    }
}
