
package org.mgnl.nicki.pdf.engine;

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

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.pdf.configuration.PdfConfiguration;
import org.mgnl.nicki.pdf.model.template.Data;
import org.mgnl.nicki.pdf.model.template.Page;
import org.mgnl.nicki.pdf.template.PdfTemplate;

@Slf4j
public class PdfEngine extends PdfPageEventHelper {
	
	private PdfConfiguration config;
	private PageRenderer pageRenderer;
	private ContentRenderer contentRenderer;
	private List<Page> pages;

	public static PdfEngine fromConfig(String configPropertyName, String contextBasePathPropertyName) throws JAXBException, IOException {
		String configValue = Config.getString(configPropertyName, "<configuration><fonts><default font=\"Helvetica\" size=\"8\" style=\"normal\"/></fonts></configuration>");
		ByteArrayInputStream bais = new ByteArrayInputStream(configValue.getBytes());
		return new PdfEngine(bais, Config.getString(contextBasePathPropertyName, "/"));
	}

	public static PdfEngine fromResource(String configResource, String contextBasePath) throws JAXBException, IOException {
		return new PdfEngine(PdfEngine.class.getClassLoader().getResourceAsStream(configResource), contextBasePath);
	}

	public static PdfEngine fromFile(String configFile, String contextBasePath) throws FileNotFoundException, JAXBException, IOException {
		InputStream is = new FileInputStream(configFile);
		return new PdfEngine(is, contextBasePath);
	}

	public PdfEngine(InputStream configStream, String contextBasePath) throws JAXBException, IOException {
		Objects.requireNonNull(configStream, "config-inputstream is null");

		config = new PdfConfiguration(configStream, contextBasePath);
		config.init();
		
		Objects.requireNonNull(config, "configuration has not been loaded properly");
	}

	@Override
	public void onEndPage(PdfWriter writer, Document document) {
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

	public void render(PdfTemplate template, OutputStream os) throws IOException {
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

	private Document initRenderEnvironment(PdfTemplate template, OutputStream os) {
		Page startPage = template.getDocument().getPages().getStartPage();
		pages = template.getDocument().getPages().getPage();
		pages.add(0, startPage);

		Document document = new Document();
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
