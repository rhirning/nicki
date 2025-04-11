
package org.mgnl.nicki.portlets;

/*-
 * #%L
 * nicki-portlets
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


import java.io.IOException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.jdom2.JDOMException;
import org.mgnl.nicki.core.util.XmlHelper;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

// TODO: Auto-generated Javadoc
/**
 * The Class Test.
 */
public class Test {

/** The Constant token. */
//	static final String token="PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVMtQVNDSUkiPz48c2FtbDpBc3NlcnRpb24geG1sbnM6c2FtbD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6MS4wOmFzc2VydGlvbiIgSXNzdWVJbnN0YW50PSIyMDE1LTA3LTE2VDE0OjEzOjI2WiIgSXNzdWVyPSJyYnBtLmlkbS5ub3ZlbGwuY29tIiBNYWpvclZlcnNpb249IjEiIE1pbm9yVmVyc2lvbj0iMSI PHNhbWw6Q29uZGl0aW9ucyBOb3RPbk9yQWZ0ZXI9IjIwMTUtMDctMTZUMTU6MTM6MjZaIi8 PHNhbWw6QXV0aGVudGljYXRpb25TdGF0ZW1lbnQgQXV0aGVudGljYXRpb25JbnN0YW50PSIyMDE1LTA3LTE2VDE0OjEzOjI2WiIgQXV0aGVudGljYXRpb25NZXRob2Q9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjEuMDphbTp1bnNwZWNpZmllZCI PHNhbWw6U3ViamVjdD48c2FtbDpOYW1lSWRlbnRpZmllciBGb3JtYXQ9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjEuMTpuYW1laWQtZm9ybWF0Olg1MDlTdWJqZWN0TmFtZSI Y249VzA3Njg5MyxvdT0wMi04MTYwLG91PTgxLG91PTAyLG91PWludGVybmFsLG91PXVzZXJzLG89cG53PC9zYW1sOk5hbWVJZGVudGlmaWVyPjwvc2FtbDpTdWJqZWN0Pjwvc2FtbDpBdXRoZW50aWNhdGlvblN0YXRlbWVudD48ZHM6U2lnbmF0dXJlIHhtbG5zOmRzPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjIj4KPGRzOlNpZ25lZEluZm8geG1sbnM6ZHM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyMiPgo8ZHM6Q2Fub25pY2FsaXphdGlvbk1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMTAveG1sLWV4Yy1jMTRuIyIgeG1sbnM6ZHM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyMiLz4KPGRzOlNpZ25hdHVyZU1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyNyc2Etc2hhMSIgeG1sbnM6ZHM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyMiLz4KPGRzOlJlZmVyZW5jZSBVUkk9IiIgeG1sbnM6ZHM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyMiPgo8ZHM6VHJhbnNmb3JtcyB4bWxuczpkcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyI CjxkczpUcmFuc2Zvcm0gQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjZW52ZWxvcGVkLXNpZ25hdHVyZSIgeG1sbnM6ZHM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyMiLz4KPGRzOlRyYW5zZm9ybSBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMTAveG1sLWV4Yy1jMTRuIyIgeG1sbnM6ZHM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyMiPjxlYzpJbmNsdXNpdmVOYW1lc3BhY2VzIHhtbG5zOmVjPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzEwL3htbC1leGMtYzE0biMiIFByZWZpeExpc3Q9ImRzIHNhbWwiLz48L2RzOlRyYW5zZm9ybT4KPC9kczpUcmFuc2Zvcm1zPgo8ZHM6RGlnZXN0TWV0aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI3NoYTEiIHhtbG5zOmRzPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjIi8 CjxkczpEaWdlc3RWYWx1ZSB4bWxuczpkcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyI eThRN3RjUlpzK0dkR1ZUZVdvWUZ6WEVDTzg4PTwvZHM6RGlnZXN0VmFsdWU CjwvZHM6UmVmZXJlbmNlPgo8L2RzOlNpZ25lZEluZm8 CjxkczpTaWduYXR1cmVWYWx1ZSB4bWxuczpkcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyI CmorY2hlMEVQRWswR2pqbVk3K0h0akFHZXV5OXQvTWlxdTJnRmxTZURubzRmZ2xOZWtUSjNIeEpPcHZXUHhpZTY5UTkrcFpHcFNiRUgKWXRGdkQvRmNkYUc2VTBMTE52VjFkbmFmbDREOG5jVHVXRlkvNDArdzkvYU1ERkw1cVZGSFYwQ2F2ZlFYUStjNHllRlRWanpGZnZSSQpHUm94NmpmMXVBUjdZUWhNdDJBMHZ4b0pvVTY3TmVhVDc3SlpBallEZ1djQkNTdVBOSVNYM0lnMW5kRWZkUWcrc0JvNTNDQk9EMnp2CjNPdTB2ZlRJSERiSEg3Q0hUR25ZYS9EemNoZ2xwaVRKVWpFM3pKRU9xUktKaEFvcHdUbmp6Y01YYnFxQk96YlNXZXhZc3lremhCRGoKZE9RQW5wNVRVUng4cjVlTzdXbnlUUlhrR0JLdytmU1l5Zlg5U2c9PQo8L2RzOlNpZ25hdHVyZVZhbHVlPgo8L2RzOlNpZ25hdHVyZT48L3NhbWw6QXNzZXJ0aW9uPg==";
	static final String token="PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVMtQVNDSUkiPz48c2FtbDpBc3NlcnRpb24geG1sbnM6c2FtbD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6MS4wOmFzc2VydGlvbiIgSXNzdWVJbnN0YW50PSIyMDE1LTA3LTE2VDExOjI1OjUzWiIgSXNzdWVyPSJyYnBtLmlkbS5ub3ZlbGwuY29tIiBNYWpvclZlcnNpb249IjEiIE1pbm9yVmVyc2lvbj0iMSI";//" PHNhbWw6Q29uZGl0aW9ucyBOb3RPbk9yQWZ0ZXI9IjIwMTUtMDctMTZUMTI6MjU6NTNaIi8 PHNhbWw6QXV0aGVudGljYXRpb25TdGF0ZW1lbnQgQXV0aGVudGljYXRpb25JbnN0YW50PSIyMDE1LTA3LTE2VDExOjI1OjUzWiIgQXV0aGVudGljYXRpb25NZXRob2Q9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjEuMDphbTp1bnNwZWNpZmllZCI PHNhbWw6U3ViamVjdD48c2FtbDpOYW1lSWRlbnRpZmllciBGb3JtYXQ9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjEuMTpuYW1laWQtZm9ybWF0Olg1MDlTdWJqZWN0TmFtZSI Y249VzA3Njg5MyxvdT0wMi04MTYwLG91PTgxLG91PTAyLG91PWludGVybmFsLG91PXVzZXJzLG89cG53PC9zYW1sOk5hbWVJZGVudGlmaWVyPjwvc2FtbDpTdWJqZWN0Pjwvc2FtbDpBdXRoZW50aWNhdGlvblN0YXRlbWVudD48ZHM6U2lnbmF0dXJlIHhtbG5zOmRzPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjIj4KPGRzOlNpZ25lZEluZm8geG1sbnM6ZHM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyMiPgo8ZHM6Q2Fub25pY2FsaXphdGlvbk1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMTAveG1sLWV4Yy1jMTRuIyIgeG1sbnM6ZHM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyMiLz4KPGRzOlNpZ25hdHVyZU1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyNyc2Etc2hhMSIgeG1sbnM6ZHM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyMiLz4KPGRzOlJlZmVyZW5jZSBVUkk9IiIgeG1sbnM6ZHM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyMiPgo8ZHM6VHJhbnNmb3JtcyB4bWxuczpkcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyI CjxkczpUcmFuc2Zvcm0gQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjZW52ZWxvcGVkLXNpZ25hdHVyZSIgeG1sbnM6ZHM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyMiLz4KPGRzOlRyYW5zZm9ybSBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMTAveG1sLWV4Yy1jMTRuIyIgeG1sbnM6ZHM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyMiPjxlYzpJbmNsdXNpdmVOYW1lc3BhY2VzIHhtbG5zOmVjPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzEwL3htbC1leGMtYzE0biMiIFByZWZpeExpc3Q9ImRzIHNhbWwiLz48L2RzOlRyYW5zZm9ybT4KPC9kczpUcmFuc2Zvcm1zPgo8ZHM6RGlnZXN0TWV0aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI3NoYTEiIHhtbG5zOmRzPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjIi8 CjxkczpEaWdlc3RWYWx1ZSB4bWxuczpkcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyI Z1dpYjB1d1FCaE1YYUIrVTBSeVo1TWlqK2VBPTwvZHM6RGlnZXN0VmFsdWU CjwvZHM6UmVmZXJlbmNlPgo8L2RzOlNpZ25lZEluZm8 CjxkczpTaWduYXR1cmVWYWx1ZSB4bWxuczpkcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyI CkpDb1ZZbGQ1SU1hT2ZrM21wV2ZrbmhXVWluejJhcGkxU1dnbVhaRnNtaWFXb3c1M3hWcDhUYk1vaDJZcmFiRGl0YlZteW1ONWZQV04KV0UrRk9helRKcStFQksvZWpUUytmTzNRQU9DY2ZlclZqODJBQkEwMmd2ZjN2QmhwdmtINFpNOXRWTm9MOW05YWxFWXVrWVkvaWR4Sgo5dzZqSno5blFLcFRTaEVzazNheXV5T0xFUTlESlR3OG5FaEhNZnRrempsVjUyVHVrR2ZpR3pEOWhEUlFHRWdwaDlnaVBWeTQvUGJaCkxubjF1Ym4wbVRSR2JsNXhUb3dxeFluRGhlNUlVQ0MyczlIek56aTFPUDVmV1lkcitYOFpzR2V2Rnk3TDY2QXNvZW9VcjIyeGtvYUsKL2pHRy93cWFHM01TV3hUdTZ5M20vZzVaYUxnaTQ1OTg1YnNhcFE9PQo8L2RzOlNpZ25hdHVyZVZhbHVlPgo8L2RzOlNpZ25hdHVyZT48L3NhbWw6QXNzZXJ0aW9uPg==";
	
	/** The Constant USER_PATH. */
	static final String USER_PATH = "/Assertion/AuthenticationStatement/Subject/NameIdentifier";
//	static final String USER_PATH = "/saml:Assertion/saml:AuthenticationStatement/saml:Subject/saml:NameIdentifier";
	/**
 * The main method.
 *
 * @param args the arguments
 * @throws XPathExpressionException the x path expression exception
 * @throws IOException Signals that an I/O exception has occurred.
 * @throws JDOMException the JDOM exception
 * @throws SAXException the SAX exception
 */
	public static void main(String[] args) throws XPathExpressionException, IOException, JDOMException, SAXException {
		byte[] decoded = Base64.decodeBase64(token.replace(" ", "").getBytes());
		String decodedToken = new String(decoded, "UTF-8");
		
		System.out.println(decodedToken);
		Document doc = XmlHelper.getDocumentFromXml(decodedToken);
//		Document doc = XmlHelper.getDocumentFromClasspath(Test.class, "SAML-Token2.xml");
		XPath xPath = XPathFactory.newInstance().newXPath();
		String result = xPath.compile(USER_PATH).evaluate(doc);
		System.out.println(result);
	}

}
