/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mgnl.nicki.ldap.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.w3c.dom.Document;
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

    private static XmlHelper instance = null;
    private Document doc;
    private XPath navigator = null;
    private DocumentBuilder db = null;

    private XmlHelper() {
        navigator = XPathFactory.newInstance().newXPath();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }

    }

    public static XmlHelper getInstance() {
        if (instance == null) {
            instance = new XmlHelper();
        }

        return instance;
    }

    public Document getNewDocument() {
        return db.newDocument();
    }
    
    public Document getDocument() {
        if(doc == null) {
            doc = getNewDocument();
        }
        
        return doc;
    }
    
    public void setDocument(Document doc) {
        this.doc = doc;
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
        Object node = null;

        try {
            XPathExpression expr = navigator.compile(xpath);
            node = expr.evaluate(ctx, XPathConstants.NODE);
        } catch (XPathExpressionException ex) {
            System.err.println("corrupted xpath expression: " + xpath + " - " + ex.getMessage());
        }

        if (clazz.isInstance(node)) {
            return (T) node;
        } else {
            return null;
        }
    }

    public Document getDocumentFromXml(String xml) throws SAXException {
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

    private String getXml(Document doc, Node node) {
        DOMImplementationLS impl = (DOMImplementationLS) doc.getImplementation();
        LSSerializer serializer = impl.createLSSerializer();
        String xml = serializer.writeToString(node);
        return StringUtils.substringAfter(xml, "\n");
    }

    public String getXml(Node node) {
        return getXml(node.getOwnerDocument(), node);
    }

    public String getXml(Document doc) {
        return getXml(doc, doc);
    }
}
