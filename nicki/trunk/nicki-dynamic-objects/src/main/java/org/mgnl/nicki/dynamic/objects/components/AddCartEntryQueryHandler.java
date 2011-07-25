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
class AddCartEntryQueryHandler implements XpathQueryHandler<Element> {

    private CartEntry entry;
    
    public AddCartEntryQueryHandler(CartEntry entry) {
        this.entry = entry;
    }

    public Node getContextNode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getXpath() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Class<Element> getResultType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void handle(List<Element> nodeList) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    
}
