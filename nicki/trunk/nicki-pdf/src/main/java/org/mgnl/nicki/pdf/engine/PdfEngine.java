package org.mgnl.nicki.pdf.engine;

import org.mgnl.nicki.pdf.template.PdfTemplate;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.mgnl.nicki.pdf.model.Box;
import org.mgnl.nicki.pdf.model.Configuration;
import org.mgnl.nicki.pdf.model.Document;
import org.mgnl.nicki.pdf.model.Image;
import org.mgnl.nicki.pdf.model.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdfEngine {

	private static final Logger log = LoggerFactory.getLogger(PdfEngine.class);
	private static float CM_PER_INCH = 2.54f;
	private static float MM_PER_USER_UNIT = CM_PER_INCH * 10.0f / 72.0f;
	private Map<String, BaseFont> fonts = new HashMap<String, BaseFont>();
	private PdfTemplate template;



	public void render(PdfTemplate template, OutputStream os) throws DocumentException, IOException {

		this.template = template;
		Document doc = template.getDocument();
		com.lowagie.text.Document document = new com.lowagie.text.Document();
		PdfWriter writer = PdfWriter.getInstance(document, os);
		document.open();

		document.setPageSize(PageSize.A4);
		log.debug("page-size: X={}, Y={}", document.getPageSize().getWidth(), document.getPageSize().getHeight());

		PdfContentByte cb = writer.getDirectContent();

		initDocumentConfiguration(doc.getConfiguration());

		cb.beginText();
		if (doc.getPage().get(0) != null) {
			for (Box box : doc.getPage().get(0).getBox()) {
				render(cb, box);
			}
		}
		cb.endText();
		document.close();
	}

	private void render(PdfContentByte cb, Box box) throws DocumentException, IOException {
		Point point = new Point(box.getX(), box.getY());
		if (box.getText() != null) {
			render(cb, box.getText(), point);
		} else {
			render(cb, box.getImage(), point, box.getVerticalAlign(), box.getAlign());
		}
	}

	private void render(PdfContentByte cb, Text text, Point p) throws DocumentException {
		log.debug("rendering text: {}", text.getValue());
		BaseFont bf = fonts.get(text.getFont());
		Font f = new Font(bf, text.getSize());

		Point posUU = transformPoint(p);
		if (text.getWidth() != null) {
			Phrase phrase = new Phrase(text.getValue(), f);
			Point llUU = new Point(transform(p.x), transform(p.y - text.getHeight()));
			Point urUU = new Point(transform(p.x + text.getWidth()), transform(p.y) + f.getCalculatedLeading(1.1f));
			log.debug("column text: lowerLeft={}, upperRight={}", llUU, urUU);

			ColumnText columnText = new ColumnText(cb);
			columnText.setSimpleColumn(llUU.x, llUU.y, urUU.x, urUU.y);
			columnText.setLeading(0, 1.2f);
			columnText.setAlignment(Element.ALIGN_LEFT);
			columnText.addText(phrase);
			columnText.go();
		} else {
			cb.setFontAndSize(bf, text.getSize());
			cb.showTextAligned(Element.ALIGN_LEFT, text.getValue(), (float) posUU.getX(), (float) posUU.getY(), 0);
		}

		log.debug("setting text position to {} (INCH: {})", p, posUU);
	}

	private void render(PdfContentByte cb, Image image, Point point, String vAlign, String align) throws DocumentException, IOException{
		log.debug("rendering with vAlign={}, align={}", vAlign, align);
		com.lowagie.text.Image pdfImage = com.lowagie.text.Image.getInstance(image.getValue());

		pdfImage.scaleAbsoluteHeight(transform(image.getHeight()));
		pdfImage.scaleAbsoluteWidth(transform(image.getWidth()));

		if (vAlign.equals("top")) {
			point = new Point(point.getX(), point.getY() - image.getHeight());
		}

		if (align.equals("center")) {
			point = new Point(point.getX() - (image.getWidth() / 2.0f), point.getY());
		}

		Point position = transformPoint(point);
		log.debug("setting image position to {} (INCH: {})", point, position);
		pdfImage.setAbsolutePosition(position.getX(), position.getY());
		cb.addImage(pdfImage);
	}

	private void initDocumentConfiguration(Configuration configuration) throws DocumentException, IOException {
		for (org.mgnl.nicki.pdf.model.Font font : configuration.getFonts().getFont()) {
			BaseFont bf = BaseFont.createFont(font.getValue(), BaseFont.CP1252, true);
			fonts.put(font.getName(), bf);
			String[] encoding = bf.getCodePagesSupported();
			log.debug("supported codepages for {}", bf.getPostscriptFontName());
			for (int i = 0; i < encoding.length; i++) {
				log.debug("[{}] {}", i, encoding[i]);

			}
		}
	}

	Point transformPoint(Point point) {
		return new Point(transform(point.getX()), (int) transform(point.getY()));
	}

	float transform(float value) {
		return (value / MM_PER_USER_UNIT);
	}

	class Point {

		float x, y;

		public Point(float x, float y) {
			this.x = x;
			this.y = y;
		}

		public Point(Point point) {
			this.x = point.x;
			this.y = point.y;
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}

		public String toString() {
			return "POINT[x:" + x + ";y:" + y + "]";
		}
	}
}
