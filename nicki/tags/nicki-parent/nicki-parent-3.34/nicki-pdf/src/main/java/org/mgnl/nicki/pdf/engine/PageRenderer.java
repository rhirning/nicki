package org.mgnl.nicki.pdf.engine;


import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.pdf.configuration.FontStyle;
import org.mgnl.nicki.pdf.configuration.PdfConfiguration;
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
		} else if (box.getImage() != null) {
			render(box.getImage(), point, box.getVerticalAlign(), box.getAlign());
		}
	}

	private void render(Text text, Point p) throws DocumentException, IOException {
		log.debug("rendering text: {}", text.getValue());
		Font f = config.getFont(text.getFont(), text.getSize(), FontStyle.byName(text.getStyle()));

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

	private void render(Image image, Point point, String vAlign, String align) throws DocumentException, IOException {
		log.debug("rendering with vAlign={}, align={}", vAlign, align);
		if(image == null) {
			return;
		}
		
		com.lowagie.text.Image pdfImage = config.getImage(image.getValue());
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