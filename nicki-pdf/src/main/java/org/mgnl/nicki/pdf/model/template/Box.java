//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2018.03.26 um 11:17:25 AM CEST 
//


package org.mgnl.nicki.pdf.model.template;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für box complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="box">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="text" type="{}text" minOccurs="0"/>
 *         &lt;element name="barcode" type="{}barcode" minOccurs="0"/>
 *         &lt;element name="image" type="{}image" minOccurs="0"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{}coordinates"/>
 *       &lt;attGroup ref="{}alignment"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "box", propOrder = {
    "text",
    "barcode",
    "image"
})
public class Box {

    protected Text text;
    protected Barcode barcode;
    protected Image image;
    @XmlAttribute(name = "x")
    protected Integer x;
    @XmlAttribute(name = "y")
    protected Integer y;
    @XmlAttribute(name = "align")
    protected String align;
    @XmlAttribute(name = "vertical-align")
    protected String verticalAlign;

    /**
     * Ruft den Wert der text-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Text }
     *     
     */
    public Text getText() {
        return text;
    }

    /**
     * Legt den Wert der text-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Text }
     *     
     */
    public void setText(Text value) {
        this.text = value;
    }

    /**
     * Ruft den Wert der barcode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Barcode }
     *     
     */
    public Barcode getBarcode() {
        return barcode;
    }

    /**
     * Legt den Wert der barcode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Barcode }
     *     
     */
    public void setBarcode(Barcode value) {
        this.barcode = value;
    }

    /**
     * Ruft den Wert der image-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Image }
     *     
     */
    public Image getImage() {
        return image;
    }

    /**
     * Legt den Wert der image-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Image }
     *     
     */
    public void setImage(Image value) {
        this.image = value;
    }

    /**
     * Ruft den Wert der x-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getX() {
        return x;
    }

    /**
     * Legt den Wert der x-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setX(Integer value) {
        this.x = value;
    }

    /**
     * Ruft den Wert der y-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getY() {
        return y;
    }

    /**
     * Legt den Wert der y-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setY(Integer value) {
        this.y = value;
    }

    /**
     * Ruft den Wert der align-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlign() {
        return align;
    }

    /**
     * Legt den Wert der align-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlign(String value) {
        this.align = value;
    }

    /**
     * Ruft den Wert der verticalAlign-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVerticalAlign() {
        return verticalAlign;
    }

    /**
     * Legt den Wert der verticalAlign-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVerticalAlign(String value) {
        this.verticalAlign = value;
    }

}
