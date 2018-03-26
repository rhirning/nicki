
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.xml.bind.JAXBElement;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.pdf.configuration.FontStyle;
import org.mgnl.nicki.pdf.configuration.PdfConfiguration;
import org.mgnl.nicki.pdf.model.template.Barcode;
import org.mgnl.nicki.pdf.model.template.Break;
import org.mgnl.nicki.pdf.model.template.Checkbox;
import org.mgnl.nicki.pdf.model.template.Data;
import org.mgnl.nicki.pdf.model.template.Image;
import org.mgnl.nicki.pdf.model.template.Item;
import org.mgnl.nicki.pdf.model.template.Link;
import org.mgnl.nicki.pdf.model.template.List;
import org.mgnl.nicki.pdf.model.template.Table;
import org.mgnl.nicki.pdf.model.template.TableData;
import org.mgnl.nicki.pdf.model.template.TableRow;
import org.mgnl.nicki.pdf.model.template.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ElementListener;
import com.itextpdf.text.Font;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class ContentRenderer {

	private static final Logger LOG = LoggerFactory.getLogger(ContentRenderer.class);
	private PdfConfiguration config;
	private Document document;
	
	private static final float PADDING_TOP = 2f;
	private static final float PADDING_BOTTOM = 4f;
	
	public ContentRenderer(PdfConfiguration config) {
		this.config = config;
	}

	public void render(ElementListener parent, Data data) throws DocumentException, IOException {
		for (Object contentElement : data.getContent()) {
			if (contentElement instanceof JAXBElement)
			{
				@SuppressWarnings("rawtypes")
				Object entry = ((JAXBElement) contentElement).getValue();
				
				if (entry instanceof Image) {
					LOG.debug("rendering image to document");
					render(parent, (Image) entry);
					LOG.debug("finished rendering image to document");
				} else if (entry instanceof Table) {
					LOG.debug("rendering table to document");
					render(parent, (Table) entry);
					LOG.debug("finished rendering table to document");
				} else if (entry instanceof Text) {
					LOG.debug("rendering text to document");
					render(parent, (Text) entry);
					LOG.debug("finished rendering text to document");
				} else if (entry instanceof Barcode) {
					LOG.debug("rendering barcode to document");
					render(parent, (Barcode) entry);
					LOG.debug("finished rendering barcode to document");
				} else if (entry instanceof Link) {
					LOG.debug("rendering link to document");
					render(parent, (Link) entry);
					LOG.debug("finished rendering link to document");
				} else if (entry instanceof List) {
					LOG.debug("rendering list to document");
					render(parent, (List) entry);
					LOG.debug("finished rendering list to document");
				} else if (entry instanceof Break) {
					LOG.debug("rendering break to document");
					addBreak();
					LOG.debug("finished rendering break to document");
				}
			}
		}
	}

	private void addBreak() {
		this.document.newPage();
	}

	public void render(ElementListener parent, List list) throws BadElementException, MalformedURLException, IOException, DocumentException {

		com.itextpdf.text.List pdfList = new com.itextpdf.text.List(list.isOrdered() != null && list.isOrdered()? 
				com.itextpdf.text.List.ORDERED:com.itextpdf.text.List.UNORDERED);
		String symbol = list.getSymbol();
		if (symbol != null) {
			pdfList.setListSymbol(symbol);
		}
		for (Item item : list.getItem()) {
			render(pdfList, item);
		}

		parent.add(pdfList);
	}
	
	private void render(com.itextpdf.text.List pdfList, Item item) throws DocumentException, IOException {
		Text title = null;
		ArrayList<Object> entries = new ArrayList<Object>();
		for (Object contentElement : item.getContent()) {
			if (contentElement instanceof JAXBElement) {
				@SuppressWarnings("rawtypes")
				JAXBElement entry = (JAXBElement) contentElement;

				if (StringUtils.equals("title", entry.getName().getLocalPart())) {
					title = (Text) entry.getValue();
				} else {
					entries.add(entry.getValue());
				}
			}
		}

		if (title != null) {
			render(pdfList, title);
			if (entries.size() > 0) {
				com.itextpdf.text.List subList = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
				//subList.setAutoindent(false);
				subList.setSymbolIndent(10);
				subList.setListSymbol("");
				render(subList, entries);
				pdfList.add(subList);
			}
		} else {
			render(pdfList, entries);
		}
		
	}

	private void render(com.itextpdf.text.List list, ArrayList<Object> entries) throws DocumentException, IOException {
		for (Object object : entries) {
			if (object instanceof Text) {
				Text text = (Text) object;
				render(list, text);
			} else if (object instanceof List) {
				List subList = (List) object;
				ListItem listItem = new ListItem();
				render(listItem, subList);
				list.add(listItem);
			}
		}
	}

	private void render(ListItem listItem, List list) throws DocumentException, IOException {
		com.itextpdf.text.List pdfList = new com.itextpdf.text.List(list.isOrdered() != null && list.isOrdered()? 
				com.itextpdf.text.List.ORDERED:com.itextpdf.text.List.UNORDERED);
		String symbol = list.getSymbol();
		if (symbol != null) {
			pdfList.setListSymbol(symbol);
		}
		for (Item item : list.getItem()) {
			render(pdfList, item);
		}

		listItem.add(pdfList);	
	}

	private void render(com.itextpdf.text.List list, Text text) throws DocumentException, IOException {
		Font f = config.getFont(text.getFont(), text.getSize(), FontStyle.byName(text.getStyle()));
		if (StringUtils.isNotBlank(text.getColor())) {
			f.setColor(ContentRenderer.getColor(text.getColor()));
		}
		ListItem listItem = new ListItem(correctText(text), f);
		list.add(listItem);
	}

	public String correctText(Text text) {
		if (StringUtils.equals(text.getFormat(), "block")) {
			StringBuilder sb = new StringBuilder();
			String[] lines = StringUtils.split(text.getValue(), "\n");
			for (String line : lines) {
				String corrected = StringUtils.strip(line);
				if (StringUtils.isNotBlank(corrected)) {
					if (sb.length() > 0) {
						sb.append(" ");
					}
					sb.append(corrected);
				}
			}
			return sb.toString();
		} else if (StringUtils.equals(text.getFormat(), "strip")) {
			StringBuilder sb = new StringBuilder();
			String[] lines = StringUtils.split(text.getValue(), "\n");
			for (String line : lines) {
				String corrected = StringUtils.strip(line);
				if (StringUtils.isNotBlank(corrected)) {
					if (sb.length() > 0) {
						sb.append("\n");
					}
					sb.append(corrected);
				}
			}
			return sb.toString();
		} else {
			return text.getValue();
		}
	}

	public void render(ElementListener parent, Image image) throws BadElementException, MalformedURLException, IOException, DocumentException {
		if (image == null) {
			return;
		}

		com.itextpdf.text.Image pdfImage = config.getImage(image.getValue());
		
		if (pdfImage == null) {
			return;
		}
		pdfImage.scaleAbsoluteHeight(config.transform(image.getHeight()));
		pdfImage.scaleAbsoluteWidth(config.transform(image.getWidth()));

		parent.add(pdfImage);
	}

	public void render(ElementListener parent, Text text) throws BadElementException, MalformedURLException, IOException, DocumentException {
		Font f = config.getFont(text.getFont(), text.getSize(), FontStyle.byName(text.getStyle()));
		if (StringUtils.isNotBlank(text.getColor())) {
			f.setColor(ContentRenderer.getColor(text.getColor()));
		}
		Paragraph p = new Paragraph(correctText(text), f);
		parent.add(p);
	}


	public void render(ElementListener parent, Barcode barcode) throws BadElementException, MalformedURLException, IOException, DocumentException {
		BarcodeQRCode barcodeQRCode = new BarcodeQRCode(barcode.getValue(), barcode.getWidth(), barcode.getHeight(), null);
		com.itextpdf.text.Image pdfImage = barcodeQRCode.getImage();
		
		parent.add(pdfImage);
	}


	public void render(ElementListener parent, Link link) throws BadElementException, MalformedURLException, IOException, DocumentException {
		Anchor a = new Anchor(link.getValue(), config.getFont(link.getFont(), link.getSize(), FontStyle.byName(link.getStyle())));
		a.setReference(link.getReference());
		parent.add(a);
	}

	public void render(ElementListener parent, Table table) throws DocumentException, IOException {
		BaseColor borderColor = getColor(table.getBorderColor());
		int colCount;
		try {
			colCount = table.getRow().get(0).getColumn().size();
		} catch (Exception e) {
			colCount = 0;
		}
		if (colCount == 0) {
			try {
				colCount = table.getHeader().getColumn().size();
			} catch (Exception e) {
				// no valid header
				colCount = 0;
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
			
			for (TableData th : table.getHeader().getColumn()) {
				render(pdfTable, borderColor, f, th);
			}
			pdfTable.setHeaderRows(1);
		}

		for (TableRow row : table.getRow()) {
			Font f = config.getFont(
					row.getFont(), 
					row.getSize(), 
					FontStyle.byName(row.getStyle()));
			for (TableData td : row.getColumn()) {
				render(pdfTable, borderColor, f, td);
			}
		}
		parent.add(pdfTable);
	}
	


	public void render(PdfPTable pdfTable, BaseColor borderColor, Font f, TableData data) throws DocumentException, IOException {
		for (Object contentElement : data.getContent()) {
			if (contentElement instanceof JAXBElement)
			{
				@SuppressWarnings("rawtypes")
				Object entry = ((JAXBElement) contentElement).getValue();
				
				if (entry instanceof Image) {
					LOG.debug("rendering image to document");
					render(pdfTable, borderColor, (Image) entry);
					LOG.debug("finished rendering image to document");
				} else if (entry instanceof Text) {
					LOG.debug("rendering text to document");
					render(pdfTable, borderColor, f, (Text) entry);
					LOG.debug("finished rendering text to document");
				} else if (entry instanceof Barcode) {
					LOG.debug("rendering barcode to document");
					render(pdfTable, borderColor, f, (Barcode) entry);
					LOG.debug("finished rendering barcode to document");
				} else if (entry instanceof Link) {
					LOG.debug("rendering link to document");
					render(pdfTable, borderColor, (Link) entry);
					LOG.debug("finished rendering link to document");
				} else if (entry instanceof Checkbox) {
					LOG.debug("rendering link to document");
					render(pdfTable, borderColor, (Checkbox) entry);
					LOG.debug("finished rendering checkbox to document");
				}
			}
		}
	}
	
	private void render(PdfPTable pdfTable, BaseColor borderColor, Image image) throws DocumentException, IOException {
		if (image == null) {
			return;
		}

		com.itextpdf.text.Image pdfImage = config.getImage(image.getValue());
		
		if (pdfImage == null) {
			return;
		}
		pdfImage.scaleAbsoluteHeight(config.transform(image.getHeight()));
		pdfImage.scaleAbsoluteWidth(config.transform(image.getWidth()));
		BaseColor backgroundColor = getColor(image.getBackgroundColor());
		PdfPCell cell = createCell(pdfImage, backgroundColor, borderColor);
		if (StringUtils.isNotBlank(image.getAlign())) {
			cell.setHorizontalAlignment(getAlignment(image.getAlign()));
		}
		if (StringUtils.isNotBlank(image.getVerticalAlign())) {
			cell.setHorizontalAlignment(getAlignment(image.getVerticalAlign()));
		}
		pdfTable.addCell(cell);
	}
	
	private void render(PdfPTable pdfTable, BaseColor borderColor, Font f, Text text) throws DocumentException, IOException {
		Font tdFont = config.getFont(f, text.getFont(), text.getSize(), FontStyle.byName(text.getStyle()));
		if (StringUtils.isNotBlank(text.getColor())) {
			tdFont.setColor(getColor(text.getColor()));
		}
		Phrase p = new Phrase(text.getValue(), tdFont);
		BaseColor backgroundColor = getColor(text.getBackgroundColor());
		PdfPCell cell = createCell(p, backgroundColor, borderColor);
		if (StringUtils.isNotBlank(text.getAlign())) {
			cell.setHorizontalAlignment(getAlignment(text.getAlign()));
		}
		if (StringUtils.isNotBlank(text.getVerticalAlign())) {
			cell.setHorizontalAlignment(getAlignment(text.getVerticalAlign()));
		}
		pdfTable.addCell(cell);
	}
	
	private void render(PdfPTable pdfTable, BaseColor borderColor, Font f, Barcode barcode) throws DocumentException, IOException {
		BarcodeQRCode barcodeQRCode = new BarcodeQRCode(barcode.getValue(), barcode.getWidth(), barcode.getHeight(), null);
        PdfPCell cell = new PdfPCell(barcodeQRCode.getImage(), true);
		pdfTable.addCell(cell);
	}
	
	public static BaseColor getColor(String color) {
		if (StringUtils.length(color) >= 7) {
			String hex = color;
			if (hex.startsWith("#")) {
				hex = StringUtils.substringAfter(hex, "#");
			}
			try {
				int r = Integer.parseInt(StringUtils.substring(hex, 0, 2), 16);
				int g = Integer.parseInt(StringUtils.substring(hex, 2, 4), 16);
				int b = Integer.parseInt(StringUtils.substring(hex, 4, 6), 16);
				return new BaseColor(r,g,b);
			} catch (Exception e) {
				LOG.error("Error parsing color", e);
			}
		}
		return new BaseColor(255,255,255);
	}

	private int getAlignment(String align) {
		if (StringUtils.equalsIgnoreCase(align, "left")) {
			return Element.ALIGN_LEFT;
		} else if (StringUtils.equalsIgnoreCase(align, "right")) {
			return Element.ALIGN_RIGHT;
		} else if (StringUtils.equalsIgnoreCase(align, "center")) {
			return Element.ALIGN_CENTER;
		} else if (StringUtils.equalsIgnoreCase(align, "bottom")) {
			return Element.ALIGN_BOTTOM;
		} else if (StringUtils.equalsIgnoreCase(align, "top")) {
			return Element.ALIGN_TOP;
		} else if (StringUtils.equalsIgnoreCase(align, "middle")) {
			return Element.ALIGN_MIDDLE;
		} else {
			return Element.ALIGN_JUSTIFIED;
		}
	}

	private void render(PdfPTable pdfTable, BaseColor borderColor, Checkbox checkbox) throws DocumentException, IOException {
		PdfPCell cell = new PdfPCell();
		cell.setBorderColor(borderColor);
		if (StringUtils.isNotBlank(checkbox.getBackgroundColor())) {
			BaseColor color = getColor(checkbox.getBackgroundColor());
			cell.setBackgroundColor(color);
		}
		if (StringUtils.isNotBlank(checkbox.getAlign())) {
			cell.setHorizontalAlignment(getAlignment(checkbox.getAlign()));
		}
		if (StringUtils.isNotBlank(checkbox.getVerticalAlign())) {
			cell.setHorizontalAlignment(getAlignment(checkbox.getVerticalAlign()));
		}

		cell.setCellEvent(new CheckboxCellEvent(checkbox));
		
		pdfTable.addCell(cell);
	}
	
	private void render(PdfPTable pdfTable, BaseColor borderColor, Link link) throws DocumentException, IOException {
		Anchor a = new Anchor(link.getValue(), config.getFont(link.getFont(), link.getSize(), FontStyle.byName(link.getStyle())));
		a.setReference(link.getReference());
		BaseColor backgroundColor = getColor(link.getBackgroundColor());
		PdfPCell cell = createCell(a, backgroundColor, borderColor);
		if (StringUtils.isNotBlank(link.getAlign())) {
			cell.setHorizontalAlignment(getAlignment(link.getAlign()));
		}
		if (StringUtils.isNotBlank(link.getVerticalAlign())) {
			cell.setHorizontalAlignment(getAlignment(link.getVerticalAlign()));
		}
		pdfTable.addCell(cell);
	}

	private PdfPCell createCell(Phrase p, BaseColor backgroundColor, BaseColor borderColor){
		PdfPCell cell = new PdfPCell(p);
		
		cell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorderColor(borderColor);
		cell.setPaddingTop(PADDING_TOP);
		cell.setPaddingBottom(PADDING_BOTTOM);
		cell.setBackgroundColor(backgroundColor);
		
		return cell;
	}

	private PdfPCell createCell(com.itextpdf.text.Image image, BaseColor backgroundColor, BaseColor borderColor){
		PdfPCell cell = new PdfPCell(image);
		
		cell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorderColor(borderColor);
		cell.setPaddingTop(PADDING_TOP);
		cell.setPaddingBottom(PADDING_BOTTOM);
		cell.setBackgroundColor(backgroundColor);
		
		return cell;
	}

	public void setDocument(Document document) {
		this.document =document;
	}
}
