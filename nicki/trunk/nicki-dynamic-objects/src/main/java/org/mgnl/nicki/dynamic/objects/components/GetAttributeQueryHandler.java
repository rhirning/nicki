/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mgnl.nicki.dynamic.objects.components;

import java.util.List;
import org.mgnl.nicki.ldap.xml.XpathQueryHandler;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

/**
 *
 * @author cna
 */
public class GetAttributeQueryHandler implements XpathQueryHandler<Attr> {

    private String xpath;
    private Node ctx;
    
    private String attrvalue;
    
    public GetAttributeQueryHandler(Node ctx, String xpath) {
        this.xpath = xpath;
        this.ctx = ctx;
    }
    
    public Node getContextNode() {
        return ctx;
    }

    public Class getResultType() {
        return Attr.class;
    }

    public String getXpath() {
        return xpath;
    }
    
    public void handle(List<Attr> nodeList) {
        attrvalue = nodeList.get(0).getValue();
    }
    
    public String getValue() {
        return attrvalue;
    }
}
