package org.mgnl.nicki.mailtemplate.test;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

public class PdfTest {
	public static void main(String[] args) throws Exception {
	    
	    StringBuffer buf = new StringBuffer();
	    buf.append("<html>");
	    
	    // put in some style
	    buf.append("<head><style language='text/css'>");
	    buf.append("h3 { border: 1px solid #aaaaff; background: #ccccff; ");
	    buf.append("padding: 1em; text-transform: capitalize; font-family: sansserif; font-weight: normal;}");
	    buf.append("p { margin: 1em 1em 4em 3em; } p:first-letter { color: red; font-size: 150%; }");
	    buf.append("h2 { background: #5555ff; color: white; border: 10px solid black; padding: 3em; font-size: 200%; }");
	    buf.append("</style></head>");
	    
	    // generate the body
	    buf.append("<body>");
	    buf.append("<p><img src='100bottles.jpg'/></p>");
	    for(int i=99; i>0; i--) {
	        buf.append("<h3>"+i+" bottles of beer on the wall, "
	                + i + " bottles of beer!</h3>");
	        buf.append("<p>Take one down and pass it around, "
	                + (i-1) + " bottles of beer on the wall</p>\n");
	    }
	    buf.append("<h2>No more bottles of beer on the wall, no more bottles of beer. ");
	    buf.append("Go to the store and buy some more, 99 bottles of beer on the wall.</h2>");
	    buf.append("</body>");
	    buf.append("</html>");

	    // parse the markup into an xml Document
	    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    Document doc = builder.parse(new ByteArrayInputStream(buf.toString().getBytes("UTF-8")));

	    ITextRenderer renderer = new ITextRenderer();
	    renderer.setDocument(doc, null);

	    String outputFile = "c:/data/100bottles.pdf";
	    OutputStream os = new FileOutputStream(outputFile);
	    renderer.layout();
	    renderer.createPDF(os);
	    os.close();
	}
}
