
package org.mgnl.nicki.xls.test;

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


import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;

public class XlsTest {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		try (Workbook wb = new HSSFWorkbook()) { // or new XSSFWorkbook();
			CreationHelper createHelper = wb.getCreationHelper();
			Sheet sheet = wb.createSheet("new sheet");
			// Create a row and put some cells in it. Rows are 0 based.
			Row row = sheet.createRow((short) 10);
			// Create a cell and put a value in it.
			Cell cell = row.createCell(0);
			cell.setCellValue(1);

			// Or do it on one line.
			row.createCell(1).setCellValue(1.2);
			row.createCell(2).setCellValue(createHelper.createRichTextString("This is a string"));
			row.createCell(9).setCellValue(true);

			row = sheet.createRow((short) 5);
			// Create a cell and put a value in it.
			cell = row.createCell(0);
			cell.setCellValue(1);

			// Or do it on one line.
			row.createCell(1).setCellValue(1.2);
			row.createCell(2).setCellValue(createHelper.createRichTextString("This is a string"));
			row.createCell(9).setCellValue(true);
			wb.createSheet("second sheet");

			// Note that sheet name is Excel must not exceed 31 characters
			// and must not contain any of the any of the following characters:
			// 0x0000
			// 0x0003
			// colon (:)
			// backslash (\)
			// asterisk (*)
			// question mark (?)
			// forward slash (/)
			// opening square bracket ([)
			// closing square bracket (])

			// You can use
			// org.apache.poi.ss.util.WorkbookUtil#createSafeSheetName(String
			// nameProposal)}
			// for a safe way to create valid names, this utility replaces
			// invalid characters with a space (' ')
			String safeName = WorkbookUtil.createSafeSheetName("[O'Brien's sales*?]"); // returns
																						// "
																						// O'Brien's
																						// sales
																						// "
			wb.createSheet(safeName);

			FileOutputStream fileOut = new FileOutputStream("D:/temp/xls/workbook.xls");
			wb.write(fileOut);
			fileOut.close();
		}
	}

}
