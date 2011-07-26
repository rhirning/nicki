/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mgnl.nicki.dynamic.objects.components;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cna
 */
public class CartEntry {

    public enum ACTION {

        ADD,
        MODIFY,
        DELETE
    }
    private String id;
    private ACTION action;
    private Map<String, String> attributes = new HashMap<String, String>();

    public ACTION getAction() {
        return action;
    }

    public String getId() {
        return id;
    }

    public CartEntry(String id, ACTION action) {
        this.id = id;
        this.action = action;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public void addAttributes(Map<String, String> attributes) {
        this.attributes.putAll(attributes);
    }

    public void addAttribute(String key, String value) {
        attributes.put(key, value);
    }

    public String getAttribute(String key) {
        return attributes.get(key);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[CARTENTRY id=");
        sb.append(id);
        sb.append(", action=");
        sb.append(action.toString());
        sb.append(", attributes=");
        sb.append(attributes);
        sb.append("]");

        return sb.toString();
    }
}
