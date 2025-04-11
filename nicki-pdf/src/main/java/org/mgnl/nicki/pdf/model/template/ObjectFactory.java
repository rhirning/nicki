//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2018.03.26 um 11:17:25 AM CEST 
//


package org.mgnl.nicki.pdf.model.template;

/*-
 * #%L
 * nicki-pdf
 * %%
 * Copyright (C) 2017 - 2018 Ralf Hirning
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
 * generated in the org.mgnl.nicki.pdf.model.template package. 
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

    /** The Constant _TableDataImage_QNAME. */
    private final static QName _TableDataImage_QNAME = new QName("", "image");
    
    /** The Constant _TableDataLink_QNAME. */
    private final static QName _TableDataLink_QNAME = new QName("", "link");
    
    /** The Constant _TableDataCheckbox_QNAME. */
    private final static QName _TableDataCheckbox_QNAME = new QName("", "checkbox");
    
    /** The Constant _TableDataText_QNAME. */
    private final static QName _TableDataText_QNAME = new QName("", "text");
    
    /** The Constant _TableDataBarcode_QNAME. */
    private final static QName _TableDataBarcode_QNAME = new QName("", "barcode");
    
    /** The Constant _DataBreak_QNAME. */
    private final static QName _DataBreak_QNAME = new QName("", "break");
    
    /** The Constant _DataList_QNAME. */
    private final static QName _DataList_QNAME = new QName("", "list");
    
    /** The Constant _DataTable_QNAME. */
    private final static QName _DataTable_QNAME = new QName("", "table");
    
    /** The Constant _ItemTitle_QNAME. */
    private final static QName _ItemTitle_QNAME = new QName("", "title");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.mgnl.nicki.pdf.model.template
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
     * Create an instance of {@link Document.Pages }
     *
     * @return the pages
     */
    public Document.Pages createDocumentPages() {
        return new Document.Pages();
    }

    /**
     * Create an instance of {@link Data }.
     *
     * @return the data
     */
    public Data createData() {
        return new Data();
    }

    /**
     * Create an instance of {@link Image }.
     *
     * @return the image
     */
    public Image createImage() {
        return new Image();
    }

    /**
     * Create an instance of {@link Item }.
     *
     * @return the item
     */
    public Item createItem() {
        return new Item();
    }

    /**
     * Create an instance of {@link Break }.
     *
     * @return the break
     */
    public Break createBreak() {
        return new Break();
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
     * Create an instance of {@link Link }.
     *
     * @return the link
     */
    public Link createLink() {
        return new Link();
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
     * Create an instance of {@link List }.
     *
     * @return the list
     */
    public List createList() {
        return new List();
    }

    /**
     * Create an instance of {@link Content }.
     *
     * @return the content
     */
    public Content createContent() {
        return new Content();
    }

    /**
     * Create an instance of {@link Checkbox }.
     *
     * @return the checkbox
     */
    public Checkbox createCheckbox() {
        return new Checkbox();
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
     * Create an instance of {@link Text }.
     *
     * @return the text
     */
    public Text createText() {
        return new Text();
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
     * Create an instance of {@link Barcode }.
     *
     * @return the barcode
     */
    public Barcode createBarcode() {
        return new Barcode();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link Image }{@code >}}.
     *
     * @param value the value
     * @return the JAXB element< image>
     */
    @XmlElementDecl(namespace = "", name = "image", scope = TableData.class)
    public JAXBElement<Image> createTableDataImage(Image value) {
        return new JAXBElement<Image>(_TableDataImage_QNAME, Image.class, TableData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Link }{@code >}}.
     *
     * @param value the value
     * @return the JAXB element< link>
     */
    @XmlElementDecl(namespace = "", name = "link", scope = TableData.class)
    public JAXBElement<Link> createTableDataLink(Link value) {
        return new JAXBElement<Link>(_TableDataLink_QNAME, Link.class, TableData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Checkbox }{@code >}}.
     *
     * @param value the value
     * @return the JAXB element< checkbox>
     */
    @XmlElementDecl(namespace = "", name = "checkbox", scope = TableData.class)
    public JAXBElement<Checkbox> createTableDataCheckbox(Checkbox value) {
        return new JAXBElement<Checkbox>(_TableDataCheckbox_QNAME, Checkbox.class, TableData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Text }{@code >}}.
     *
     * @param value the value
     * @return the JAXB element< text>
     */
    @XmlElementDecl(namespace = "", name = "text", scope = TableData.class)
    public JAXBElement<Text> createTableDataText(Text value) {
        return new JAXBElement<Text>(_TableDataText_QNAME, Text.class, TableData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Barcode }{@code >}}.
     *
     * @param value the value
     * @return the JAXB element< barcode>
     */
    @XmlElementDecl(namespace = "", name = "barcode", scope = TableData.class)
    public JAXBElement<Barcode> createTableDataBarcode(Barcode value) {
        return new JAXBElement<Barcode>(_TableDataBarcode_QNAME, Barcode.class, TableData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Image }{@code >}}.
     *
     * @param value the value
     * @return the JAXB element< image>
     */
    @XmlElementDecl(namespace = "", name = "image", scope = Data.class)
    public JAXBElement<Image> createDataImage(Image value) {
        return new JAXBElement<Image>(_TableDataImage_QNAME, Image.class, Data.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Break }{@code >}}.
     *
     * @param value the value
     * @return the JAXB element< break>
     */
    @XmlElementDecl(namespace = "", name = "break", scope = Data.class)
    public JAXBElement<Break> createDataBreak(Break value) {
        return new JAXBElement<Break>(_DataBreak_QNAME, Break.class, Data.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Link }{@code >}}.
     *
     * @param value the value
     * @return the JAXB element< link>
     */
    @XmlElementDecl(namespace = "", name = "link", scope = Data.class)
    public JAXBElement<Link> createDataLink(Link value) {
        return new JAXBElement<Link>(_TableDataLink_QNAME, Link.class, Data.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Text }{@code >}}.
     *
     * @param value the value
     * @return the JAXB element< text>
     */
    @XmlElementDecl(namespace = "", name = "text", scope = Data.class)
    public JAXBElement<Text> createDataText(Text value) {
        return new JAXBElement<Text>(_TableDataText_QNAME, Text.class, Data.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code >}}.
     *
     * @param value the value
     * @return the JAXB element< list>
     */
    @XmlElementDecl(namespace = "", name = "list", scope = Data.class)
    public JAXBElement<List> createDataList(List value) {
        return new JAXBElement<List>(_DataList_QNAME, List.class, Data.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Barcode }{@code >}}.
     *
     * @param value the value
     * @return the JAXB element< barcode>
     */
    @XmlElementDecl(namespace = "", name = "barcode", scope = Data.class)
    public JAXBElement<Barcode> createDataBarcode(Barcode value) {
        return new JAXBElement<Barcode>(_TableDataBarcode_QNAME, Barcode.class, Data.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Table }{@code >}}.
     *
     * @param value the value
     * @return the JAXB element< table>
     */
    @XmlElementDecl(namespace = "", name = "table", scope = Data.class)
    public JAXBElement<Table> createDataTable(Table value) {
        return new JAXBElement<Table>(_DataTable_QNAME, Table.class, Data.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Text }{@code >}}.
     *
     * @param value the value
     * @return the JAXB element< text>
     */
    @XmlElementDecl(namespace = "", name = "text", scope = Item.class)
    public JAXBElement<Text> createItemText(Text value) {
        return new JAXBElement<Text>(_TableDataText_QNAME, Text.class, Item.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Text }{@code >}}.
     *
     * @param value the value
     * @return the JAXB element< text>
     */
    @XmlElementDecl(namespace = "", name = "title", scope = Item.class)
    public JAXBElement<Text> createItemTitle(Text value) {
        return new JAXBElement<Text>(_ItemTitle_QNAME, Text.class, Item.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code >}}.
     *
     * @param value the value
     * @return the JAXB element< list>
     */
    @XmlElementDecl(namespace = "", name = "list", scope = Item.class)
    public JAXBElement<List> createItemList(List value) {
        return new JAXBElement<List>(_DataList_QNAME, List.class, Item.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Barcode }{@code >}}.
     *
     * @param value the value
     * @return the JAXB element< barcode>
     */
    @XmlElementDecl(namespace = "", name = "barcode", scope = Item.class)
    public JAXBElement<Barcode> createItemBarcode(Barcode value) {
        return new JAXBElement<Barcode>(_TableDataBarcode_QNAME, Barcode.class, Item.class, value);
    }

}
