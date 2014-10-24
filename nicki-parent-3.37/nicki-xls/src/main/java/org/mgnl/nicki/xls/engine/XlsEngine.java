
package org.mgnl.nicki.xls.engine;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.mgnl.nicki.xls.model.template.Box;
import org.mgnl.nicki.xls.model.template.Document;
import org.mgnl.nicki.xls.model.template.Link;
import org.mgnl.nicki.xls.model.template.Page;
import org.mgnl.nicki.xls.model.template.Style;
import org.mgnl.nicki.xls.model.template.Table;
import org.mgnl.nicki.xls.model.template.TableData;
import org.mgnl.nicki.xls.model.template.TableRow;
import org.mgnl.nicki.xls.model.template.Text;
import org.mgnl.nicki.xls.template.XlsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XlsEngine {
	private static final Logger log = LoggerFactory.getLogger(XlsEngine.class);
	public static final String HYPER_LINK = "HYPER_LINK";

	private Workbook wb;
	private Map<String, CellStyle> cellStyles = new HashMap<String, CellStyle>();

	public XlsEngine()  {
	}
	
	public void render(InputStream master, XlsTemplate xlsTemplate, OutputStream os) throws IOException {
		log.debug("rendering xls");
		//if (template.)
		if (master != null) {
			wb = new HSSFWorkbook(master);
		}
		if (wb == null) {
			wb = new HSSFWorkbook();  // or new XSSFWorkbook();
		}
		Document document = xlsTemplate.getDocument();
		
		createDefaultStyles();
		if (document.getStyles() != null) {
			List<Style> styles = document.getStyles().getStyle();
			for (Style style : styles) {
				cellStyles.put(style.getName(), createCellStyle(style));
			}
		}
		
		List<Page> pages = document.getPages().getPage();
		
		int i = 0;
		for (Page page : pages) {
			i++;
			render(wb, page, i);
		}

	    wb.write(os);
	}

	private void createDefaultStyles() {
		CellStyle hlink_style = wb.createCellStyle();
		Font hlink_font = wb.createFont();
		hlink_font.setUnderline(Font.U_SINGLE);
		hlink_font.setColor(IndexedColors.BLUE.getIndex());
		hlink_style.setFont(hlink_font);

		cellStyles.put(HYPER_LINK, hlink_style);
		
	}

	private CellStyle createCellStyle(Style style) {
		CellStyle cellStyle =  wb.createCellStyle();

		if (style.getAlign() != null) {
			ALIGN.setAlign(cellStyle, style.getAlign());
		}
		if (style.getVerticalAlign() != null) {
			ALIGN.setVerticalAlign(cellStyle, style.getVerticalAlign());
		}
		
		return cellStyle;
	}

	private void render(Workbook wb, Page page, int pageNum) {
		String pageName = page.getName();
		if (StringUtils.isBlank(pageName)) {
			pageName = "sheet " + pageNum;
		}
		Sheet sheet = wb.getSheet(pageName);
		if (sheet == null) {
			sheet = wb.createSheet(pageName);
		}
		
		render (sheet, page);
		
	}

	private void render(Sheet sheet, Page page) {
		if (page.getBox() != null) {
			for (Box box : page.getBox()) {
				render (sheet, box);
			}
		}
		if (page.getTable() != null) {
			render (sheet, page.getTable());
		}
		
	}

	public enum ALIGN {
		ALIGN_GENERAL			(CellStyle.ALIGN_GENERAL),
		ALIGN_LEFT				(CellStyle.ALIGN_LEFT),
		ALIGN_CENTER			(CellStyle.ALIGN_CENTER),
		ALIGN_RIGHT				(CellStyle.ALIGN_RIGHT),
		ALIGN_FILL				(CellStyle.ALIGN_FILL),
		ALIGN_JUSTIFY			(CellStyle.ALIGN_JUSTIFY),
		ALIGN_CENTER_SELECTION	(CellStyle.ALIGN_CENTER_SELECTION),
		VERTICAL_TOP			(CellStyle.VERTICAL_TOP),
		VERTICAL_BOTTOM			(CellStyle.VERTICAL_BOTTOM),
		VERTICAL_CENTER			(CellStyle.VERTICAL_CENTER),
		VERTICAL_JUSTIFY		(CellStyle.VERTICAL_JUSTIFY);
		
		short value;
		ALIGN(short value) {
			this.value = value;
		}
		
		private static void setAlign(CellStyle cellStyle, String alignString, String pattern) {
			for (ALIGN align : values()) {
				String name = StringUtils.substringAfter(align.name(), pattern);
				if (StringUtils.equalsIgnoreCase(name, alignString)) {
					cellStyle.setAlignment(align.value);
				}
			}
		}
		
		public static void setAlign(CellStyle cellStyle, String alignString) {
			setAlign(cellStyle, alignString, "ALIGN_");
		}
		
		public static void setVerticalAlign(CellStyle cellStyle, String alignString) {
			setAlign(cellStyle, alignString, "VERTICAL_");
		}
	}


	private void render(Sheet sheet, Table table) {
		int startX = table.getX()!= null ? table.getX() -1 : 0;
		int starty = table.getY()!= null ? table.getY() -1 : 0;
		if (table.getRow() != null) {
			int y = starty;
			for (TableRow tableRow : table.getRow()) {
				int x = startX;
				Row row = sheet.createRow(y);
				y++;
				if (tableRow.getColumn() != null) {
					for (TableData tableData : tableRow.getColumn()) {
						if (tableData.getContent() != null) {
							for(Object contentElement : tableData.getContent()) {
								if (contentElement instanceof JAXBElement)
								{
									@SuppressWarnings("rawtypes")
									Object entry = ((JAXBElement) contentElement).getValue();
									Cell cell = row.createCell(x);
									if (tableData.getStyle() != null) {
										setCellStyle(cell, tableData.getStyle());
									}
									
									if (entry instanceof Text) {
										log.debug("rendering text to document");
										render(cell, (Text) entry);
										log.debug("finished rendering text to document");
									} else if (entry instanceof Link) {
										log.debug("rendering link to document");
										render(cell, (Link) entry);
										log.debug("finished rendering link to document");
									}
								}

							}
						}
						x++;
					}
				}
				
			}
		}
	}

	private void render(Cell cell, Link entry) {
		Hyperlink link = wb.getCreationHelper().createHyperlink(Hyperlink.LINK_URL);
	    link.setAddress(entry.getReference());
	    cell.setHyperlink(link);
		cell.setCellStyle(cellStyles.get(HYPER_LINK));
	}

	private void render(Cell cell, Text text) {
		cell.setCellValue(text.getValue());
	}

	private void render(Sheet sheet, Box box) {
		if (box.getText() != null) {
			int rowNumber = box.getY() - 1;
			Row row = sheet.getRow(rowNumber);
			if (row == null) {
				row = sheet.createRow(box.getY() - 1);
			}
			int colNumber = box.getX() - 1;
			Cell cell = row.getCell(colNumber);
			if (cell == null) {
				cell = row.createCell(colNumber);
			}
			cell.setCellValue(box.getText().getValue());
			if (box.getStyle() != null) {
				setCellStyle(cell, box.getStyle());
			}
		}
	}

	private void setCellStyle(Cell cell, String style) {
		if (cellStyles.containsKey(style)) {
			cell.setCellStyle(cellStyles.get(style));
		}
	}

}
