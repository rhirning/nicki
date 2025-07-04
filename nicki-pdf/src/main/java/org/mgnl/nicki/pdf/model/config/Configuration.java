
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.04.10 at 10:15:45 AM CEST 
//


package org.mgnl.nicki.pdf.model.config;

/*-
 * #%L
 * nicki-pdf
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


import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;



/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fonts" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="default" type="{}default-font"/>
 *                   &lt;element name="font" type="{}font" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "fonts"
})
@XmlRootElement(name = "configuration")
public class Configuration {

    /** The fonts. */
    protected Configuration.Fonts fonts;

    /**
     * Gets the value of the fonts property.
     * 
     * @return
     *     possible object is
     *     {@link Configuration.Fonts }
     *     
     */
    public Configuration.Fonts getFonts() {
        return fonts;
    }

    /**
     * Sets the value of the fonts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Configuration.Fonts }
     *     
     */
    public void setFonts(Configuration.Fonts value) {
        this.fonts = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="default" type="{}default-font"/>
     *         &lt;element name="font" type="{}font" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "_default",
        "font"
    })
    public static class Fonts {

        /** The default. */
        @XmlElement(name = "default", required = true)
        protected DefaultFont _default;
        
        /** The font. */
        @XmlElement(required = true)
        protected List<Font> font;

        /**
         * Gets the value of the default property.
         * 
         * @return
         *     possible object is
         *     {@link DefaultFont }
         *     
         */
        public DefaultFont getDefault() {
            return _default;
        }

        /**
         * Sets the value of the default property.
         * 
         * @param value
         *     allowed object is
         *     {@link DefaultFont }
         *     
         */
        public void setDefault(DefaultFont value) {
            this._default = value;
        }

        /**
         * Gets the value of the font property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the font property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getFont().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Font }
         *
         * @return the font
         */
        public List<Font> getFont() {
            if (font == null) {
                font = new ArrayList<Font>();
            }
            return this.font;
        }

    }

}
