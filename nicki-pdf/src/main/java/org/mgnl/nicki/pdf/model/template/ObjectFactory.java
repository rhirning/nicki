//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2018.03.26 um 11:17:25 AM CEST 
//


package org.mgnl.nicki.pdf.model.template;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


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

    private final static QName _TableDataImage_QNAME = new QName("", "image");
    private final static QName _TableDataLink_QNAME = new QName("", "link");
    private final static QName _TableDataCheckbox_QNAME = new QName("", "checkbox");
    private final static QName _TableDataText_QNAME = new QName("", "text");
    private final static QName _TableDataBarcode_QNAME = new QName("", "barcode");
    private final static QName _DataBreak_QNAME = new QName("", "break");
    private final static QName _DataList_QNAME = new QName("", "list");
    private final static QName _DataTable_QNAME = new QName("", "table");
    private final static QName _ItemTitle_QNAME = new QName("", "title");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.mgnl.nicki.pdf.model.template
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Document }
     * 
     */
    public Document createDocument() {
        return new Document();
    }

    /**
     * Create an instance of {@link Document.Pages }
     * 
     */
    public Document.Pages createDocumentPages() {
        return new Document.Pages();
    }

    /**
     * Create an instance of {@link Data }
     * 
     */
    public Data createData() {
        return new Data();
    }

    /**
     * Create an instance of {@link Image }
     * 
     */
    public Image createImage() {
        return new Image();
    }

    /**
     * Create an instance of {@link Item }
     * 
     */
    public Item createItem() {
        return new Item();
    }

    /**
     * Create an instance of {@link Break }
     * 
     */
    public Break createBreak() {
        return new Break();
    }

    /**
     * Create an instance of {@link TableData }
     * 
     */
    public TableData createTableData() {
        return new TableData();
    }

    /**
     * Create an instance of {@link Link }
     * 
     */
    public Link createLink() {
        return new Link();
    }

    /**
     * Create an instance of {@link Box }
     * 
     */
    public Box createBox() {
        return new Box();
    }

    /**
     * Create an instance of {@link List }
     * 
     */
    public List createList() {
        return new List();
    }

    /**
     * Create an instance of {@link Content }
     * 
     */
    public Content createContent() {
        return new Content();
    }

    /**
     * Create an instance of {@link Checkbox }
     * 
     */
    public Checkbox createCheckbox() {
        return new Checkbox();
    }

    /**
     * Create an instance of {@link Page }
     * 
     */
    public Page createPage() {
        return new Page();
    }

    /**
     * Create an instance of {@link Text }
     * 
     */
    public Text createText() {
        return new Text();
    }

    /**
     * Create an instance of {@link TableRow }
     * 
     */
    public TableRow createTableRow() {
        return new TableRow();
    }

    /**
     * Create an instance of {@link Barcode }
     * 
     */
    public Barcode createBarcode() {
        return new Barcode();
    }

    /**
     * Create an instance of {@link Table }
     * 
     */
    public Table createTable() {
        return new Table();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Image }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "image", scope = TableData.class)
    public JAXBElement<Image> createTableDataImage(Image value) {
        return new JAXBElement<Image>(_TableDataImage_QNAME, Image.class, TableData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Link }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "link", scope = TableData.class)
    public JAXBElement<Link> createTableDataLink(Link value) {
        return new JAXBElement<Link>(_TableDataLink_QNAME, Link.class, TableData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Checkbox }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "checkbox", scope = TableData.class)
    public JAXBElement<Checkbox> createTableDataCheckbox(Checkbox value) {
        return new JAXBElement<Checkbox>(_TableDataCheckbox_QNAME, Checkbox.class, TableData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Text }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "text", scope = TableData.class)
    public JAXBElement<Text> createTableDataText(Text value) {
        return new JAXBElement<Text>(_TableDataText_QNAME, Text.class, TableData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Barcode }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "barcode", scope = TableData.class)
    public JAXBElement<Barcode> createTableDataBarcode(Barcode value) {
        return new JAXBElement<Barcode>(_TableDataBarcode_QNAME, Barcode.class, TableData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Image }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "image", scope = Data.class)
    public JAXBElement<Image> createDataImage(Image value) {
        return new JAXBElement<Image>(_TableDataImage_QNAME, Image.class, Data.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Break }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "break", scope = Data.class)
    public JAXBElement<Break> createDataBreak(Break value) {
        return new JAXBElement<Break>(_DataBreak_QNAME, Break.class, Data.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Link }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "link", scope = Data.class)
    public JAXBElement<Link> createDataLink(Link value) {
        return new JAXBElement<Link>(_TableDataLink_QNAME, Link.class, Data.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Text }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "text", scope = Data.class)
    public JAXBElement<Text> createDataText(Text value) {
        return new JAXBElement<Text>(_TableDataText_QNAME, Text.class, Data.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "list", scope = Data.class)
    public JAXBElement<List> createDataList(List value) {
        return new JAXBElement<List>(_DataList_QNAME, List.class, Data.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Barcode }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "barcode", scope = Data.class)
    public JAXBElement<Barcode> createDataBarcode(Barcode value) {
        return new JAXBElement<Barcode>(_TableDataBarcode_QNAME, Barcode.class, Data.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Table }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "table", scope = Data.class)
    public JAXBElement<Table> createDataTable(Table value) {
        return new JAXBElement<Table>(_DataTable_QNAME, Table.class, Data.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Text }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "text", scope = Item.class)
    public JAXBElement<Text> createItemText(Text value) {
        return new JAXBElement<Text>(_TableDataText_QNAME, Text.class, Item.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Text }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "title", scope = Item.class)
    public JAXBElement<Text> createItemTitle(Text value) {
        return new JAXBElement<Text>(_ItemTitle_QNAME, Text.class, Item.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "list", scope = Item.class)
    public JAXBElement<List> createItemList(List value) {
        return new JAXBElement<List>(_DataList_QNAME, List.class, Item.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Barcode }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "barcode", scope = Item.class)
    public JAXBElement<Barcode> createItemBarcode(Barcode value) {
        return new JAXBElement<Barcode>(_TableDataBarcode_QNAME, Barcode.class, Item.class, value);
    }

}
