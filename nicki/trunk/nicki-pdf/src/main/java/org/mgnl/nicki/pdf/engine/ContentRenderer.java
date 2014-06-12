
package org.mgnl.nicki.pdf.engine;

import com.lowagie.text.Anchor;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;

import javax.xml.bind.JAXBElement;

import org.mgnl.nicki.pdf.configuration.FontStyle;
import org.mgnl.nicki.pdf.configuration.PdfConfiguration;
import org.mgnl.nicki.pdf.model.template.Checkbox;
import org.mgnl.nicki.pdf.model.template.Data;
import org.mgnl.nicki.pdf.model.template.Image;
import org.mgnl.nicki.pdf.model.template.Link;
import org.mgnl.nicki.pdf.model.template.Table;
import org.mgnl.nicki.pdf.model.template.TableData;
import org.mgnl.nicki.pdf.model.template.TableRow;
import org.mgnl.nicki.pdf.model.template.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentRenderer {

	private static final Logger log = LoggerFactory.getLogger(ContentRenderer.class);
	private PdfConfiguration config;
	private Document document;
	
	private static final float PADDING_TOP = 2f;
	private static final float PADDING_BOTTOM = 4f;
	
	public ContentRenderer(Document document, PdfConfiguration config) {
		this.document = document;
		this.config = config;
	}

	public void render(Data data) throws DocumentException, IOException {
		for (Object contentElement : data.getContent()) {
			if (contentElement instanceof JAXBElement)
			{
				@SuppressWarnings("rawtypes")
				Object entry = ((JAXBElement) contentElement).getValue();
				
				if (entry instanceof Image) {
					log.debug("rendering image to document");
					render((Image) entry);
					log.debug("finished rendering image to document");
				} else if (entry instanceof Table) {
					log.debug("rendering table to document");
					render((Table) entry);
					log.debug("finished rendering table to document");
				} else if (entry instanceof Text) {
					log.debug("rendering text to document");
					render((Text) entry);
					log.debug("finished rendering text to document");
				} else if (entry instanceof Link) {
					log.debug("rendering link to document");
					render((Link) entry);
					log.debug("finished rendering link to document");
				}
			}
		}
	}

	public void render(Image image) throws BadElementException, MalformedURLException, IOException, DocumentException {
		if (image == null) {
			return;
		}

		com.lowagie.text.Image pdfImage = config.getImage(image.getValue());
		
		if (pdfImage == null) {
			return;
		}
		pdfImage.scaleAbsoluteHeight(config.transform(image.getHeight()));
		pdfImage.scaleAbsoluteWidth(config.transform(image.getWidth()));

		document.add(pdfImage);
	}

	public void render(Text text) throws BadElementException, MalformedURLException, IOException, DocumentException {

		Paragraph p = new Paragraph(text.getValue(), config.getFont(text.getFont(), text.getSize(), FontStyle.byName(text.getStyle())));
		document.add(p);
	}

	public void render(Link link) throws BadElementException, MalformedURLException, IOException, DocumentException {
		Anchor a = new Anchor(link.getValue(), config.getFont(link.getFont(), link.getSize(), FontStyle.byName(link.getStyle())));
		a.setReference(link.getReference());
		document.add(a);
	}

	public void render(Table table) throws DocumentException, IOException {
		int colCount = 0;
		try {
			colCount = table.getRow().get(0).getColumn().size();
		} catch (Exception e) {
			// no row
		}
		if (colCount == 0) {
			try {
				colCount = table.getHeader().getColumn().size();
			} catch (Exception e) {
				// no valid header
			}
		}
		if (colCount == 0) {
			return;
		}
		PdfPTable pdfTable = new PdfPTable(colCount);
		pdfTable.setWidthPercentage(100f);

		if (table.getHeader() != null) {
			int widths[] = new int[colCount];
			int c = 0;
			boolean setWidth = true;
			for (TableData th : table.getHeader().getColumn()) {
				if (th.getWidth() != null) {
					widths[c] = th.getWidth();
				} else {
					setWidth = false;
					break;
				}
				c++;
			}
			if (setWidth) {
				pdfTable.setWidths(widths);
			}
			
			Font f = config.getFont(
					table.getHeader().getFont(), 
					table.getHeader().getSize(), 
					FontStyle.byName(table.getHeader().getStyle()));
			
			int i = 1;
			for (TableData th : table.getHeader().getColumn()) {
				render(pdfTable, i, f, th);
				/*
				Font thFont = config.getFont(f, th.getFont(), th.getSize(), FontStyle.byName(th.getStyle()));
				Phrase p = new Phrase(th.getValue(), thFont);
				PdfPCell cell = createCell(p, (i % 2) == 0, Color.black);
				pdfTable.addCell(cell);
				*/
				i++;
			}
			pdfTable.setHeaderRows(1);
		}

		for (Iterator<TableRow> it = table.getRow().iterator(); it.hasNext();) {
			TableRow row = it.next();
			Font f = config.getFont(
					row.getFont(), 
					row.getSize(), 
					FontStyle.byName(row.getStyle()));
			int j = 1;
			for (TableData td : row.getColumn()) {
				render(pdfTable, j, f, td);
				j++;
			}
		}
		document.add(pdfTable);
	}
	


	public void render(PdfPTable pdfTable, int columnNumber, Font f, TableData data) throws DocumentException, IOException {
		for (Object contentElement : data.getContent()) {
			if (contentElement instanceof JAXBElement)
			{
				@SuppressWarnings("rawtypes")
				Object entry = ((JAXBElement) contentElement).getValue();
				
				if (entry instanceof Image) {
					log.debug("rendering image to document");
					render(pdfTable, columnNumber, f, (Image) entry);
					log.debug("finished rendering image to document");
				} else if (entry instanceof Text) {
					log.debug("rendering text to document");
					render(pdfTable, columnNumber, f, (Text) entry);
					log.debug("finished rendering text to document");
				} else if (entry instanceof Link) {
					log.debug("rendering link to document");
					render(pdfTable, columnNumber, f, (Link) entry);
					log.debug("finished rendering link to document");
				} else if (entry instanceof Checkbox) {
					log.debug("rendering link to document");
					render(pdfTable, columnNumber, f, (Checkbox) entry);
					log.debug("finished rendering checkbox to document");
				}
			}
		}
	}
	
	private void render(PdfPTable pdfTable, int columnNumber, Font f, Image image) throws DocumentException, IOException {
		if (image == null) {
			return;
		}

		com.lowagie.text.Image pdfImage = config.getImage(image.getValue());
		
		if (pdfImage == null) {
			return;
		}
		pdfImage.scaleAbsoluteHeight(config.transform(image.getHeight()));
		pdfImage.scaleAbsoluteWidth(config.transform(image.getWidth()));
		PdfPCell cell = createCell(pdfImage, (columnNumber % 2) == 0, new Color(132, 132, 132));
		pdfTable.addCell(cell);
	}
	
	private void render(PdfPTable pdfTable, int columnNumber, Font f, Text text) throws DocumentException, IOException {
		Font tdFont = config.getFont(f, text.getFont(), text.getSize(), FontStyle.byName(text.getStyle()));
		Phrase p = new Phrase(text.getValue(), tdFont);
		PdfPCell cell = createCell(p, (columnNumber % 2) == 0, new Color(132, 132, 132));
		pdfTable.addCell(cell);
	}
	
	private void render(PdfPTable pdfTable, int columnNumber, Font f, Checkbox checkbox) throws DocumentException, IOException {
		PdfPCell cell = new PdfPCell();
		cell.setCellEvent(new CheckboxCellEvent(checkbox));
		
		pdfTable.addCell(cell);
	}
	
	private void render(PdfPTable pdfTable, int columnNumber, Font f, Link link) throws DocumentException, IOException {
		Anchor a = new Anchor(link.getValue(), config.getFont(link.getFont(), link.getSize(), FontStyle.byName(link.getStyle())));
		a.setReference(link.getReference());
		PdfPCell cell = createCell(a, (columnNumber % 2) == 0, new Color(132, 132, 132));
		pdfTable.addCell(cell);
	}

	private PdfPCell createCell(Phrase p, boolean setBackgroundColor, Color borderColor){
		PdfPCell cell = new PdfPCell(p);
		
		cell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
		cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
		cell.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
		cell.setBorderColor(borderColor);
		cell.setPaddingTop(PADDING_TOP);
		cell.setPaddingBottom(PADDING_BOTTOM);
		if (setBackgroundColor){
			cell.setBackgroundColor(new Color(210, 210, 210));
		}
		
		return cell;
	}

	private PdfPCell createCell(com.lowagie.text.Image image, boolean setBackgroundColor, Color borderColor){
		PdfPCell cell = new PdfPCell(image);
		
		cell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
		cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
		cell.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
		cell.setBorderColor(borderColor);
		cell.setPaddingTop(PADDING_TOP);
		cell.setPaddingBottom(PADDING_BOTTOM);
		if (setBackgroundColor){
			cell.setBackgroundColor(new Color(210, 210, 210));
		}
		
		return cell;
	}
}
