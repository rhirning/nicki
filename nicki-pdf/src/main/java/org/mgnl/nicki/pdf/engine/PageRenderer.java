
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


import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.pdf.configuration.FontStyle;
import org.mgnl.nicki.pdf.configuration.PdfConfiguration;
import org.mgnl.nicki.pdf.model.template.Barcode;
import org.mgnl.nicki.pdf.model.template.Box;
import org.mgnl.nicki.pdf.model.template.Image;
import org.mgnl.nicki.pdf.model.template.Page;
import org.mgnl.nicki.pdf.model.template.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

public class PageRenderer {
	private static final Logger log = LoggerFactory.getLogger(PageRenderer.class);
	private PdfContentByte content;
	private PdfWriter writer;
	
	private PdfConfiguration config;
	
	public PageRenderer(PdfWriter writer, PdfConfiguration config) {
		this.writer = writer;
		this.config = config;
	}
	
	public  void render(Page page) throws DocumentException, IOException {
		this.content = writer.getDirectContent();

		for (Box box : page.getBox()) {
			render(box);
		}
	}

	private void render(Box box) throws DocumentException, IOException {
		Point point = new Point(box.getX(), box.getY());
		if (box.getText() != null) {
			render(box.getText(), point);
		} else if (box.getBarcode() != null) {
				render(box.getBarcode(), point);
		} else if (box.getImage() != null) {
			render(box.getImage(), point, box.getVerticalAlign(), box.getAlign());
		}
	}

	private void render(Text text, Point p) throws DocumentException, IOException {
		log.debug("rendering text: {}", text.getValue());
		Font f = config.getFont(text.getFont(), text.getSize(), FontStyle.byName(text.getStyle()));

		if (StringUtils.isNotBlank(text.getColor())) {
			f.setColor(ContentRenderer.getColor(text.getColor()));
		}
		String string = StringUtils.replace(text.getValue(), "{pageNumber}",  "" + content.getPdfWriter().getPageNumber());
		
		Point posUU = config.transformPoint(p);
		if (text.getWidth() != null) {
			Phrase phrase = new Phrase(string, f);
			Point llUU = new Point(config.transform(p.x), config.transform(p.y - text.getHeight()));
			Point urUU = new Point(config.transform(p.x + text.getWidth()), config.transform(p.y) + f.getCalculatedLeading(1.1f));
			log.debug("column text: lowerLeft={}, upperRight={}", llUU, urUU);

			ColumnText columnText = new ColumnText(content);
			columnText.setSimpleColumn(llUU.x, llUU.y, urUU.x, urUU.y);
			columnText.setLeading(0, 1.2f);
			columnText.setAlignment(Element.ALIGN_LEFT);
			columnText.addText(phrase);
			columnText.go();
		} else {
			content.beginText();
			content.setFontAndSize(f.getBaseFont(), f.getSize());
			content.showTextAligned(Element.ALIGN_LEFT, string, (float) posUU.getX(), (float) posUU.getY(), 0);
			content.endText();
		}

		log.debug("setting text position to {} (INCH: {})", p, posUU);
	}

	private void render(Barcode barcode, Point point) throws DocumentException, IOException {
		log.debug("rendering barcode: {}", barcode.getValue());


		BarcodeQRCode barcodeQRCode = new BarcodeQRCode(barcode.getValue(), barcode.getWidth(), barcode.getHeight(), null);

		com.itextpdf.text.Image pdfImage = barcodeQRCode.getImage();
		if(pdfImage == null) {
			return;
		}
		
		Point position = config.transformPoint(point);
		log.debug("setting image position to {} (INCH: {})", point, position);
		pdfImage.setAbsolutePosition(position.getX(), position.getY());
		content.addImage(pdfImage);

	}

	private void render(Image image, Point point, String vAlign, String align) throws DocumentException, IOException {
		log.debug("rendering with vAlign={}, align={}", vAlign, align);
		if(image == null) {
			return;
		}
		
		com.itextpdf.text.Image pdfImage = config.getImage(image.getValue());
		if(pdfImage == null) {
			return;
		}
		
		float orgHeight = pdfImage.getHeight();
		float orgWidth = pdfImage.getWidth();

		if (image.getHeight() != null && image.getWidth() != null) {
			float w = config.transform(image.getWidth());
			float h = config.transform(image.getHeight());
			pdfImage.scaleAbsolute(w,h);
		} else if (image.getHeight() != null) {
			float h = config.transform(image.getHeight());
			float w = h*orgWidth/orgHeight;
			pdfImage.scaleAbsolute(w,h);
		} else if (image.getWidth() != null) {
			float w = config.transform(image.getWidth());
			float h = w*orgHeight/orgWidth;
			pdfImage.scaleAbsolute(w,h);
		}

		if (vAlign.equals("top")) {
			point = new Point(point.getX(), point.getY() - config.unTransform(pdfImage.getHeight()));
		}

		if (align.equals("center")) {
			point = new Point(point.getX() - (config.unTransform(pdfImage.getWidth()) / 2.0f), point.getY());
		}

		Point position = config.transformPoint(point);
		log.debug("setting image position to {} (INCH: {})", point, position);
		pdfImage.setAbsolutePosition(position.getX(), position.getY());
		content.addImage(pdfImage);
	}

}
