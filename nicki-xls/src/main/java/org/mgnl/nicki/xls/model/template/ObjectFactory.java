//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.1-b171012.0423 
//         See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
//         Any modifications to this file will be lost upon recompilation of the source schema. 
//         Generated on: 2018.11.13 at 08:19:58 AM GMT 
//


package org.mgnl.nicki.xls.model.template;

/*-
 * #%L
 * nicki-xls
 * %%
 * Copyright (C) 2017 - 2023 Ralf Hirning
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
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


// TODO: Auto-generated Javadoc
/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.mgnl.nicki.xls.model.template package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    /** The Constant _TableDataText_QNAME. */
    private final static QName _TableDataText_QNAME = new QName("", "text");
    
    /** The Constant _TableDataLink_QNAME. */
    private final static QName _TableDataLink_QNAME = new QName("", "link");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.mgnl.nicki.xls.model.template
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Document }.
     *
     * @return the document
     */
    public Document createDocument() {
        return new Document();
    }

    /**
     * Create an instance of {@link Document.Styles }
     *
     * @return the styles
     */
    public Document.Styles createDocumentStyles() {
        return new Document.Styles();
    }

    /**
     * Create an instance of {@link Document.Pages }
     *
     * @return the pages
     */
    public Document.Pages createDocumentPages() {
        return new Document.Pages();
    }

    /**
     * Create an instance of {@link Page }.
     *
     * @return the page
     */
    public Page createPage() {
        return new Page();
    }

    /**
     * Create an instance of {@link Style }.
     *
     * @return the style
     */
    public Style createStyle() {
        return new Style();
    }

    /**
     * Create an instance of {@link Box }.
     *
     * @return the box
     */
    public Box createBox() {
        return new Box();
    }

    /**
     * Create an instance of {@link Text }.
     *
     * @return the text
     */
    public Text createText() {
        return new Text();
    }

    /**
     * Create an instance of {@link Link }.
     *
     * @return the link
     */
    public Link createLink() {
        return new Link();
    }

    /**
     * Create an instance of {@link Table }.
     *
     * @return the table
     */
    public Table createTable() {
        return new Table();
    }

    /**
     * Create an instance of {@link TableRow }.
     *
     * @return the table row
     */
    public TableRow createTableRow() {
        return new TableRow();
    }

    /**
     * Create an instance of {@link TableData }.
     *
     * @return the table data
     */
    public TableData createTableData() {
        return new TableData();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Text }{@code >}.
     *
     * @param value     Java instance representing xml element's value.
     * @return     the new instance of {@link JAXBElement }{@code <}{@link Text }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "text", scope = TableData.class)
    public JAXBElement<Text> createTableDataText(Text value) {
        return new JAXBElement<Text>(_TableDataText_QNAME, Text.class, TableData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Link }{@code >}.
     *
     * @param value     Java instance representing xml element's value.
     * @return     the new instance of {@link JAXBElement }{@code <}{@link Link }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "link", scope = TableData.class)
    public JAXBElement<Link> createTableDataLink(Link value) {
        return new JAXBElement<Link>(_TableDataLink_QNAME, Link.class, TableData.class, value);
    }

}
