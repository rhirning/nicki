package org.mgnl.nicki.template.pdf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mgnl.nicki.ldap.context.AppContext;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import com.lowagie.text.DocumentException;

public class PdfTemplateRenderer {
	private static PdfTemplateRenderer instance = new PdfTemplateRenderer();

	public void render(InputStream inputStream, OutputStream outputStream)
		throws ParserConfigurationException, SAXException, IOException, DocumentException {
	    
	    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    Document doc = builder.parse(inputStream);

	    ITextRenderer renderer = new ITextRenderer();
	    renderer.setDocument(doc, null);

	    renderer.layout();
	    renderer.createPDF(outputStream);
	    outputStream.close();
	}
/*	
	public File render(String xhtml, String fileName) {
		File file = File.createTempFile(fileName, "pdf");
		
		return file;
		
	}
*/
	
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

	    String outputFile = "c:/data/100bottles.pdf";
	    OutputStream os = new FileOutputStream(outputFile);
    
	    getInstance().render(new ByteArrayInputStream(buf.toString().getBytes("UTF-8")), os);
	    os.close();
	}

	public static PdfTemplateRenderer getInstance() {
		return instance;
	}

}
