//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2018.03.26 um 11:17:25 AM CEST 
//


package org.mgnl.nicki.pdf.model.template;

import java.io.Serializable;
import java.util.ArrayList;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für data complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="data">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="text" type="{}text"/>
 *         &lt;element name="barcode" type="{}barcode"/>
 *         &lt;element name="image" type="{}image"/>
 *         &lt;element name="table" type="{}table"/>
 *         &lt;element name="list" type="{}list"/>
 *         &lt;element name="link" type="{}link"/>
 *         &lt;element name="break" type="{}break"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "data", propOrder = {
    "content"
})
public class Data {

    @XmlElementRefs({
        @XmlElementRef(name = "table", type = JAXBElement.class),
        @XmlElementRef(name = "text", type = JAXBElement.class),
        @XmlElementRef(name = "list", type = JAXBElement.class),
        @XmlElementRef(name = "break", type = JAXBElement.class),
        @XmlElementRef(name = "image", type = JAXBElement.class),
        @XmlElementRef(name = "barcode", type = JAXBElement.class),
        @XmlElementRef(name = "link", type = JAXBElement.class)
    })
    @XmlMixed
    protected java.util.List<Serializable> content;

    /**
     * Gets the value of the content property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link Table }{@code >}
     * {@link JAXBElement }{@code <}{@link Text }{@code >}
     * {@link JAXBElement }{@code <}{@link org.mgnl.nicki.pdf.model.template.List }{@code >}
     * {@link JAXBElement }{@code <}{@link Break }{@code >}
     * {@link JAXBElement }{@code <}{@link Image }{@code >}
     * {@link JAXBElement }{@code <}{@link Barcode }{@code >}
     * {@link String }
     * {@link JAXBElement }{@code <}{@link Link }{@code >}
     * 
     * 
     */
    public java.util.List<Serializable> getContent() {
        if (content == null) {
            content = new ArrayList<Serializable>();
        }
        return this.content;
    }

}
