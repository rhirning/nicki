/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mgnl.nicki.ldap.xml;

import java.io.ByteArrayInputStream;
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
public class XmlHelper implements java.io.Serializable {

	private static DocumentBuilder getDocBuilder() throws ParserConfigurationException {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		return dbf.newDocumentBuilder();
	}

	private static XPath getXpath() {
		return XPathFactory.newInstance().newXPath();
	}

	public static Document getNewDocument() {
		try {
			return getDocBuilder().newDocument();
		} catch (Exception e) {
			return null;
		}
	}

	public static <T extends Node> List<T> selectNodes(Class<T> clazz, Node ctx, String xpath) {
		//TODO XPATH parsen und $ELEM_* && $ATTR_* ersetzen durch Konstanten
		NodeList nodes;
		List<T> list = new ArrayList<T>();

		try {
			XPathExpression expr = getXpath().compile(xpath);
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

	public static <T extends Node> T selectNode(Class<T> clazz, Node ctx, String xpath) {
		Object node = null;

		try {
			XPathExpression expr = getXpath().compile(xpath);
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

	public static Document getDocumentFromXml(String xml) throws SAXException {
		if (null == xml) {
			xml = "";
		}

		InputStream is = new ByteArrayInputStream(xml.getBytes());

		Document document = null;
		try {

			document = getDocBuilder().parse(is);

		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}

		return document;

	}

	private static String getXml(Document doc, Node node) {
		DOMImplementationLS impl = (DOMImplementationLS) doc.getImplementation();
		LSSerializer serializer = impl.createLSSerializer();
		String xml = serializer.writeToString(node);
		return StringUtils.substringAfter(xml, "\n");
	}

	public String getXml(Node node) {
		return getXml(node.getOwnerDocument(), node);
	}

	public static String getXml(Document doc) {
		return getXml(doc, doc);
	}
}
/**
 *
 * @author cna
 */
/** @SuppressWarnings("serial")
public class XmlHelper implements Serializable{

    private static XmlHelper instance = null;

    private XmlHelper() {
        //navigator = XPathFactory.newInstance().newXPath();
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

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
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
            document = new SAXBuilder().build(sr);
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

}
**/