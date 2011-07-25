/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mgnl.nicki.dynamic.objects.components;

import java.util.List;
import org.mgnl.nicki.ldap.xml.XpathQueryHandler;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author cna
 */
public class SetAttributeQueryHandler implements XpathQueryHandler<Element> {

    private String xpath;
    private Node ctx;
    
    private String attrname;
    private String attrvalue;
    
    public SetAttributeQueryHandler(String xpath, Node ctx, String attrname, String attrvalue) {
        this.xpath = xpath;
        this.ctx = ctx;
    }
    
    public Node getContextNode() {
        return ctx;
    }

    public Class getResultType() {
        return Element.class;
    }

    public String getXpath() {
        return xpath;
    }
    
    public void handle(List<Element> nodeList) {
        for (Element element : nodeList) {
            element.setAttribute(attrname, attrvalue);
        }
    }
}
