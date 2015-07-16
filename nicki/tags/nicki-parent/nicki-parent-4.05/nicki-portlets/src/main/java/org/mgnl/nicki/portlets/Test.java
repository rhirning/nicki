package org.mgnl.nicki.portlets;

import java.io.IOException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jdom.JDOMException;
import org.mgnl.nicki.core.util.XmlHelper;
import org.w3c.dom.Document;

public class Test {

	static final String USER_PATH = "/Assertion/AuthenticationStatement/Subject/NameIdentifier";
//	static final String USER_PATH = "/saml:Assertion/saml:AuthenticationStatement/saml:Subject/saml:NameIdentifier";
	/**
	 * @param args
	 * @throws XPathExpressionException 
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public static void main(String[] args) throws XPathExpressionException, IOException, JDOMException {
		Document doc = XmlHelper.getDocumentFromClasspath(Test.class, "SAML-Token.xml");
		XPath xPath = XPathFactory.newInstance().newXPath();
		String result = xPath.compile(USER_PATH).evaluate(doc);
		System.out.println(result);
	}

}
