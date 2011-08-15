/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mgnl.nicki.ldap.xml;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Parent;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

/**
 *
 * @author cna
 */
@SuppressWarnings("serial")
public class XmlHelper implements Serializable{

    private static XmlHelper instance = null;
    private Document doc;
    private XPath navigator = null;
    private SAXBuilder sb = null;

    private XmlHelper() {
        //navigator = XPathFactory.newInstance().newXPath();

        sb = new SAXBuilder();
    }

    public static XmlHelper getInstance() {
        if (instance == null) {
            instance = new XmlHelper();
        }

        return instance;
    }

    public Document getNewDocument() {
        return new Document();
    }

    public Document getDocument() {
        if (doc == null) {
            doc = getNewDocument();
        }

        return doc;
    }

    public void setDocument(Document doc) {
        this.doc = doc;
    }

    public <T extends Content> List<T> selectNodes(Class<T> clazz, Parent ctx, String xpath) {
        List<T> list = new ArrayList<T>();
        List<Object> nodes;

        try {
            nodes = XPath.selectNodes(ctx, xpath);
        } catch (JDOMException ex) {
            System.err.println("corrupted xpath expression: " + xpath + " - " + ex.getMessage());
            return list;
        }

        for (Object node : nodes) {
            if (clazz.isInstance(node)) {
                list.add((T) node);
            }
        }

        return list;

    }

    public <T extends Content> T selectNode(Class<T> clazz, Parent ctx, String xpath) {
        Object node = null;

         try {
            node = XPath.selectSingleNode(ctx, xpath);
        } catch (JDOMException ex) {
            System.err.println("corrupted xpath expression: " + xpath + " - " + ex.getMessage());
            return null;
        }

        if (clazz.isInstance(node)) {
            return (T) node;
        } else {
            return null;
        }
    }

    public Document getDocumentFromXml(String xml) throws JDOMException {
        if (null == xml) {
            xml = "";
        }

        StringReader sr = new StringReader(xml);

        Document document;
        try {
            document = sb.build(sr);
        } catch (IOException ex) {
            return null;
        }

        return document;

    }

    public String getXml(Document doc) {
        XMLOutputter printer = new XMLOutputter();
        Format format = printer.getFormat();
        format.setIndent("\t");
        printer.setFormat(format);
        
        return StringUtils.substringAfter(printer.outputString(doc), format.getLineSeparator());
    }

    public String getXml(Element elem) {
        XMLOutputter printer = new XMLOutputter();
        Format format = printer.getFormat();
        format.setIndent("\t");
        printer.setFormat(format);
        
        return StringUtils.substringAfter(printer.outputString(doc), format.getLineSeparator());
    }
}
