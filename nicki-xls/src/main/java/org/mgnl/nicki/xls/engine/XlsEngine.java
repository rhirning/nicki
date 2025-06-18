
package org.mgnl.nicki.xls.engine;

/*-
 * #%L
 * nicki-xls
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
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.xml.bind.JAXBElement;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mgnl.nicki.core.helper.DataHelper;
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
import lombok.extern.slf4j.Slf4j;


/**
 * The Class XlsEngine.
 */
@Slf4j
public class XlsEngine {
	
	/** The Constant HYPER_LINK. */
	public static final String HYPER_LINK = "HYPER_LINK";

	/** The wb. */
	private Workbook wb;
	
	/** The cell styles. */
	private Map<String, CellStyle> cellStyles = new HashMap<String, CellStyle>();

	/**
	 * Instantiates a new xls engine.
	 */
	public XlsEngine()  {
	}

	/**
	 * Render.
	 *
	 * @param master the master
	 * @param xlsTemplate the xls template
	 * @param os the os
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Deprecated
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

	/**
	 * Render xlsx.
	 *
	 * @param master the master
	 * @param xlsTemplate the xls template
	 * @param os the os
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void renderXlsx(InputStream master, XlsTemplate xlsTemplate, OutputStream os) throws IOException {
		log.debug("rendering xls");
		//if (template.)
		if (master != null) {
			wb = new XSSFWorkbook(master);
		}
		if (wb == null) {
			wb = new XSSFWorkbook();  // or new XSSFWorkbook();
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

	/**
	 * Creates the default styles.
	 */
	private void createDefaultStyles() {
		CellStyle hlinkStyle = wb.createCellStyle();
		Font hlink_font = wb.createFont();
		hlink_font.setUnderline(Font.U_SINGLE);
		hlink_font.setColor(IndexedColors.BLUE.getIndex());
		hlinkStyle.setFont(hlink_font);

		cellStyles.put(HYPER_LINK, hlinkStyle);

	}

	/**
	 * Creates the cell style.
	 *
	 * @param style the style
	 * @return the cell style
	 */
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

	/**
	 * Render.
	 *
	 * @param wb the wb
	 * @param page the page
	 * @param pageNum the page num
	 */
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

	/**
	 * Render.
	 *
	 * @param sheet the sheet
	 * @param page the page
	 */
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

	/**
	 * The Enum ALIGN.
	 */
	public enum ALIGN {
		
		/** The align general. */
		ALIGN_GENERAL			(HorizontalAlignment.GENERAL),
		
		/** The align left. */
		ALIGN_LEFT				(HorizontalAlignment.LEFT),
		
		/** The align center. */
		ALIGN_CENTER			(HorizontalAlignment.CENTER),
		
		/** The align right. */
		ALIGN_RIGHT				(HorizontalAlignment.RIGHT),
		
		/** The align fill. */
		ALIGN_FILL				(HorizontalAlignment.FILL),
		
		/** The align justify. */
		ALIGN_JUSTIFY			(HorizontalAlignment.JUSTIFY),
		
		/** The align center selection. */
		ALIGN_CENTER_SELECTION	(HorizontalAlignment.CENTER_SELECTION),
		
		/** The vertical top. */
		VERTICAL_TOP			(VerticalAlignment.TOP),
		
		/** The vertical bottom. */
		VERTICAL_BOTTOM			(VerticalAlignment.BOTTOM),
		
		/** The vertical middle. */
		VERTICAL_MIDDLE			(VerticalAlignment.CENTER),
		
		/** The vertical justify. */
		VERTICAL_JUSTIFY		(VerticalAlignment.JUSTIFY);

		/** The h value. */
		HorizontalAlignment hValue;
		
		/** The v value. */
		VerticalAlignment vValue;
		
		/**
		 * Instantiates a new align.
		 *
		 * @param hValue the h value
		 */
		ALIGN(HorizontalAlignment hValue) {
			this.hValue = hValue;
		}
		
		/**
		 * Instantiates a new align.
		 *
		 * @param vValue the v value
		 */
		ALIGN(VerticalAlignment vValue) {
			this.vValue = vValue;
		}

		/**
		 * Sets the align.
		 *
		 * @param cellStyle the cell style
		 * @param alignString the align string
		 * @param pattern the pattern
		 */
		private static void setAlign(CellStyle cellStyle, String alignString, String pattern) {
			for (ALIGN align : values()) {
				String name = StringUtils.substringAfter(align.name(), pattern);
				if (StringUtils.equalsIgnoreCase(name, alignString) && align.hValue != null) {
					cellStyle.setAlignment(align.hValue);
				}
				if (StringUtils.equalsIgnoreCase(name, alignString) && align.vValue != null) {
					cellStyle.setVerticalAlignment(align.vValue);
				}
			}
		}

		/**
		 * Sets the align.
		 *
		 * @param cellStyle the cell style
		 * @param alignString the align string
		 */
		public static void setAlign(CellStyle cellStyle, String alignString) {
			setAlign(cellStyle, alignString, "ALIGN_");
		}

		/**
		 * Sets the vertical align.
		 *
		 * @param cellStyle the cell style
		 * @param alignString the align string
		 */
		public static void setVerticalAlign(CellStyle cellStyle, String alignString) {
			setAlign(cellStyle, alignString, "VERTICAL_");
		}
	}


	/**
	 * Render.
	 *
	 * @param sheet the sheet
	 * @param table the table
	 */
	private void render(Sheet sheet, Table table) {
		int startX = table.getX()!= null ? table.getX() -1 : 0;
		int starty = table.getY()!= null ? table.getY() -1 : 0;
		if (table.getRow() != null) {
			int y = starty;
			for (TableRow tableRow : table.getRow()) {
				int x = startX;
				Row row = sheet.getRow(y);
				if (row == null) {
					row = sheet.createRow(y);
				}
				y++;
				if (tableRow.getColumn() != null) {
					for (TableData tableData : tableRow.getColumn()) {
						if (tableData.getContent() != null) {
							for(Object contentElement : tableData.getContent()) {
								if (contentElement instanceof JAXBElement)
								{
									@SuppressWarnings("rawtypes")
									Object entry = ((JAXBElement) contentElement).getValue();
									Cell cell = row.getCell(x);
									if (cell == null) {
										cell = row.createCell(x);
									}
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

	/**
	 * Render.
	 *
	 * @param cell the cell
	 * @param entry the entry
	 */
	private void render(Cell cell, Link entry) {
		Hyperlink link = wb.getCreationHelper().createHyperlink(HyperlinkType.URL);
		link.setAddress(entry.getReference());
		cell.setHyperlink(link);
		cell.setCellStyle(cellStyles.get(HYPER_LINK));
	}
	
	/**
	 * The Enum TEXT_TYPE.
	 */
	enum TEXT_TYPE {
		
		/** The text. */
		TEXT {
			@Override
			void renderType(Cell cell, Text text) {
				cell.setCellValue(text.getValue());
			}
		}, 
 /** The boolean. */
 BOOLEAN {
			@Override
			void renderType(Cell cell, Text text) {
				cell.setCellValue(DataHelper.booleanOf(text.getValue()));
			}
		}, 
 /** The double. */
 DOUBLE {
			@Override
			void renderType(Cell cell, Text text) {
				String value = text.getValue();
				value = StringUtils.replace(value, ",", ".");
				if (StringUtils.isNotBlank(value)) {
					try {
						cell.setCellValue(Double.parseDouble(value));
					} catch (NumberFormatException e) {
						log.error("Error parsing double: " + text.getValue() + " -> " + e.getMessage());
					}
				}
			}
		}, 
 /** The date. */
 DATE {
			@Override
			void renderType(Cell cell, Text text) {
				try {
					cell.setCellValue(DataHelper.dateFromDisplayDay(text.getValue()));
				} catch (ParseException e) {
					log.error("Error parsing date String: " + text.getValue() + " -> " + e.getMessage());
				}
			}
		};
		
		/**
		 * Render.
		 *
		 * @param cell the cell
		 * @param text the text
		 */
		static void render(Cell cell, Text text) {
			getType(text).renderType(cell, text);
		}

		/**
		 * Render type.
		 *
		 * @param cell the cell
		 * @param text the text
		 */
		abstract void renderType(Cell cell, Text text);

		/**
		 * Gets the type.
		 *
		 * @param text the text
		 * @return the type
		 */
		static TEXT_TYPE getType(Text text) {
			for (TEXT_TYPE type : values()) {
				if (StringUtils.equalsIgnoreCase(type.name(), text.getType())) {
					return type;
				}
			}
			return TEXT;
		}
	}

	/**
	 * Render.
	 *
	 * @param cell the cell
	 * @param text the text
	 */
	private void render(Cell cell, Text text) {
		TEXT_TYPE.render(cell, text);
	}

	/**
	 * Render.
	 *
	 * @param sheet the sheet
	 * @param box the box
	 */
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

	/**
	 * Sets the cell style.
	 *
	 * @param cell the cell
	 * @param style the style
	 */
	private void setCellStyle(Cell cell, String style) {
		if (cellStyles.containsKey(style)) {
			cell.setCellStyle(cellStyles.get(style));
		}
	}

}
