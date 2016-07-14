
package org.mgnl.nicki.pdf.engine;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.util.Objects;
import org.mgnl.nicki.pdf.configuration.PdfConfiguration;
import org.mgnl.nicki.pdf.model.template.Data;
import org.mgnl.nicki.pdf.model.template.Page;
import org.mgnl.nicki.pdf.template.PdfTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdfEngine extends PdfPageEventHelper {
	
	

	private static final Logger log = LoggerFactory.getLogger(PdfEngine.class);
	
	private PdfConfiguration config;
	private PageRenderer pageRenderer = null;
	private ContentRenderer contentRenderer = null;
	private List<Page> pages;

	public static PdfEngine fromConfig(String configPropertyName, String contextBasePathPropertyName) throws JAXBException, DocumentException, IOException {
		String configValue = Config.getProperty(configPropertyName, "<configuration><fonts><default font=\"Helvetica\" size=\"8\" style=\"normal\"/></fonts></configuration>");
		ByteArrayInputStream bais = new ByteArrayInputStream(configValue.getBytes());
		return new PdfEngine(bais, Config.getProperty(contextBasePathPropertyName, "/"));
	}

	public static PdfEngine fromResource(String configResource, String contextBasePath) throws JAXBException, DocumentException, IOException {
		return new PdfEngine(PdfEngine.class.getClassLoader().getResourceAsStream(configResource), contextBasePath);
	}

	public static PdfEngine fromFile(String configFile, String contextBasePath) throws FileNotFoundException, JAXBException, DocumentException, IOException {
		InputStream is = new FileInputStream(configFile);
		return new PdfEngine(is, contextBasePath);
	}

	public PdfEngine(InputStream configStream, String contextBasePath) throws JAXBException, DocumentException, IOException {
		Objects.requireNonNull(configStream, "config-inputstream is null");

		config = new PdfConfiguration(configStream, contextBasePath);		
		
		Objects.requireNonNull(config, "configuration has not been loaded properly");
	}

	@Override
	public void onEndPage(PdfWriter writer, com.lowagie.text.Document document) {
		log.debug("end page called");
		super.onStartPage(writer, document);
		Page currentPage = null;
		Page nextPage = null;

		if (pages.size() > 1) {
			currentPage = pages.remove(0);
			nextPage = pages.get(0);
		} else {
			currentPage = pages.get(0);
			nextPage = pages.get(0);
		}
		
		try {
			pageRenderer.render(currentPage);
		} catch (DocumentException ex) {
			log.error("could not render page");
		} catch (IOException ex) {
			log.error("could not write to file");
		}
		
		
		if(log.isDebugEnabled()) {
			log.debug("setting margins to: {}, {}, {}, {}", new Object[]{
					config.transform(nextPage.getContent().getX()),
					document.getPageSize().getWidth() - config.transform(nextPage.getContent().getX() + nextPage.getContent().getWidth()),
					document.getPageSize().getHeight() - config.transform(nextPage.getContent().getY() + nextPage.getContent().getHeight()),
					config.transform(nextPage.getContent().getY())});
		}

		setMargins(document, nextPage);
	}

	public void render(PdfTemplate template, OutputStream os) throws DocumentException, IOException {
		log.debug("rendering pdf");
		Objects.requireNonNull(template, "template may not be null");
		Objects.requireNonNull(template.getDocument(), "template.document may not be null");
		Objects.requireNonNull(template.getDocument().getData(), "template.document.data may not be null");
		Data data = template.getDocument().getData();
		
		Document document = initRenderEnvironment(template, os);
		contentRenderer.setDocument(document);
		
		document.open();
		contentRenderer.render(document, data);
		document.close();


	}

	private Document initRenderEnvironment(PdfTemplate template, OutputStream os) throws DocumentException {
		Page startPage = template.getDocument().getPages().getStartPage();
		pages = template.getDocument().getPages().getPage();
		pages.add(0, startPage);

		com.lowagie.text.Document document = new com.lowagie.text.Document();
		PdfWriter writer = PdfWriter.getInstance(document, os);
		pageRenderer = new PageRenderer(writer, config);
		contentRenderer = new ContentRenderer(config);
		writer.setPageEvent(this);
		
		document.setPageSize(getSize(template));
		setMargins(document, startPage);
		return document;
	}
	
	public Rectangle getSize(PdfTemplate template) {
		String docSize = template.getDocument().getSize();
		if (StringUtils.isBlank(docSize)) {
			return PageSize.A4;
		}
		
		docSize = docSize.trim().toUpperCase();
    	int pos = docSize.indexOf(' ');
    	if (pos == -1) {
    		return PageSize.getRectangle(docSize);
    	} else {
    		String width = docSize.substring(0, pos);
    		String height = docSize.substring(pos + 1);
    		return new Rectangle(config.transform(Float.parseFloat(width)), config.transform(Float.parseFloat(height)));
    	}
	}
	
	private void setMargins(Document document, Page page) {
		document.setMargins(
				config.transform(page.getContent().getX()),
				document.getPageSize().getWidth() - config.transform(page.getContent().getX() + page.getContent().getWidth()),
				document.getPageSize().getHeight() - config.transform(page.getContent().getY() + page.getContent().getHeight()),
				config.transform(page.getContent().getY()));
	}
}
