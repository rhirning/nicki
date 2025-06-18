
package org.mgnl.nicki.core.util;

/*-
 * #%L
 * nicki-core
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
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jdom2.JDOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;


/**
 * The Class XmlHelper.
 *
 * @author cna
 */
public class XmlHelper implements java.io.Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -766315560688135875L;

	/**
	 * Gets the doc builder.
	 *
	 * @return the doc builder
	 * @throws ParserConfigurationException the parser configuration exception
	 */
	private static DocumentBuilder getDocBuilder() throws ParserConfigurationException {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		return dbf.newDocumentBuilder();
	}

	/**
	 * Gets the xpath.
	 *
	 * @return the xpath
	 */
	private static XPath getXpath() {
		return XPathFactory.newInstance().newXPath();
	}

	/**
	 * Gets the new document.
	 *
	 * @return the new document
	 */
	public static Document getNewDocument() {
		try {
			return getDocBuilder().newDocument();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Select nodes.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @param ctx the ctx
	 * @param xpath the xpath
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
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

	/**
	 * Select node.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @param ctx the ctx
	 * @param xpath the xpath
	 * @return the t
	 */
	@SuppressWarnings("unchecked")
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
	
	/**
	 * Gets the document from classpath.
	 *
	 * @param refClass the ref class
	 * @param classLoaderPath the class loader path
	 * @return the document from classpath
	 * @throws JDOMException the JDOM exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	static public Document getDocumentFromClasspath(Class<?> refClass, String classLoaderPath) throws JDOMException, IOException {
		Document document = null;
		try {
			InputStream is = refClass.getClassLoader().getResourceAsStream(classLoaderPath);
			document = getDocBuilder().parse(is);

		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}

		return document;
	}

	/**
	 * Gets the document from xml.
	 *
	 * @param xml the xml
	 * @return the document from xml
	 * @throws SAXException the SAX exception
	 */
	public static Document getDocumentFromXml(String xml) throws SAXException {
		if (null == xml) {
			xml = "";
		}


		Document document = null;
		try {
			InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			document = getDocBuilder().parse(is);

		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}

		return document;

	}

	/**
	 * Gets the document from url.
	 *
	 * @param url the url
	 * @return the document from url
	 * @throws SAXException the SAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws UnsupportedOperationException the unsupported operation exception
	 * @throws ParserConfigurationException the parser configuration exception
	 */
	public static Document getDocumentFromUrl(String url) throws SAXException, IOException, UnsupportedOperationException, ParserConfigurationException {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		try(CloseableHttpClient httpclient = HttpClients.createDefault()) {
			HttpGet httpGet = new HttpGet(url);
			try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
				//System.out.println(response1.getStatusLine());
			    HttpEntity entity1 = response1.getEntity();
			    // do something useful with the response body
			    // and ensure it is fully consumed
//			    EntityUtils.consume(entity1);
				Document document = null;
				document = getDocBuilder().parse(entity1.getContent());

				return document;
			}
		}
	}

	/**
	 * Gets the xml.
	 *
	 * @param doc the doc
	 * @param node the node
	 * @return the xml
	 */
	private static String getXml(Document doc, Node node) {
		DOMImplementationLS impl = (DOMImplementationLS) doc.getImplementation();
		LSSerializer serializer = impl.createLSSerializer();
		String xml = serializer.writeToString(node);
		return StringUtils.substringAfter(xml, ">");
	}

	/**
	 * Gets the xml.
	 *
	 * @param node the node
	 * @return the xml
	 */
	public String getXml(Node node) {
		return getXml(node.getOwnerDocument(), node);
	}

	/**
	 * Gets the xml.
	 *
	 * @param doc the doc
	 * @return the xml
	 */
	public static String getXml(Document doc) {
		return getXml(doc, doc.getDocumentElement());
	}
}
/**
 *
 * @author cna
 */
/** @SuppressWarnings("serial")
public class XmlHelper implements Serializable{

    private static XmlHelper instance;

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
