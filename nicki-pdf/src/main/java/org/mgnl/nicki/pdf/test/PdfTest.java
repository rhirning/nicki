
package org.mgnl.nicki.pdf.test;

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

 
import java.io.FileOutputStream;
import java.io.IOException;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
 

/**
 * The Class PdfTest.
 */
public class PdfTest {
 
    /** The Constant RESULT. */
    public static final String RESULT
        = "d:/temp/pdfTest.pdf";
 
    /**
     * Main method.
     *
     * @param args the arguments
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void main(String[] args)
        throws IOException {
    	// step 1
        Document document = new Document();
        // step 2
        PdfWriter writer
            = PdfWriter.getInstance(document, new FileOutputStream(RESULT));
        // step 3
        document.open();
        // step 4
        Chunk c;
        String foobar = "Foobar Film Festival";
        // Measuring a String in Helvetica
        // Measuring a String in Times
        BaseFont bf_times = BaseFont.createFont(
            "c:/windows/fonts/times.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED);
        Font times = new Font(bf_times, 12);
        float width_times = bf_times.getWidthPoint(foobar, 12);
        c = new Chunk(foobar + ": " + width_times, times);
		
        document.add(new Paragraph(c));
        document.add(new Paragraph(String.format("Chunk width: %f", c.getWidthPoint())));
        document.add(Chunk.NEWLINE);
        // Kerned text
        document.add(new Paragraph(c));
        // Drawing lines to see where the text is added
        PdfContentByte canvas = writer.getDirectContent();
        canvas.saveState();
        canvas.setLineWidth(0.05f);
        canvas.moveTo(400, 806);
        canvas.lineTo(400, 626);
        canvas.moveTo(508.7f, 806);
        canvas.lineTo(508.7f, 626);
        canvas.moveTo(280, 788);
        canvas.lineTo(520, 788);
        canvas.moveTo(280, 752);
        canvas.lineTo(520, 752);
        canvas.moveTo(280, 716);
        canvas.lineTo(520, 716);
        canvas.moveTo(280, 680);
        canvas.lineTo(520, 680);
        canvas.moveTo(280, 644);
        canvas.lineTo(520, 644);
        canvas.stroke();
        canvas.restoreState();
        // Adding text with PdfContentByte.showTextAligned()
        canvas.beginText();
        Font font = new Font(Font.HELVETICA);
        BaseFont bf = font.getCalculatedBaseFont(false);
        canvas.setFontAndSize(bf, 12);
        canvas.showTextAligned(Element.ALIGN_LEFT, foobar, 400, 788, 0);
        canvas.showTextAligned(Element.ALIGN_RIGHT, foobar, 400, 752, 0);
        canvas.showTextAligned(Element.ALIGN_CENTER, foobar, 400, 716, 0);
        canvas.showTextAligned(Element.ALIGN_CENTER, foobar, 400, 680, 30);
        canvas.showTextAlignedKerned(Element.ALIGN_LEFT, foobar, 400, 644, 0);
        canvas.endText();
        // More lines to see where the text is added
        canvas.saveState();
        canvas.setLineWidth(0.05f);
        canvas.moveTo(200, 590);
        canvas.lineTo(200, 410);
        canvas.moveTo(400, 590);
        canvas.lineTo(400, 410);
        canvas.moveTo(80, 572);
        canvas.lineTo(520, 572);
        canvas.moveTo(80, 536);
        canvas.lineTo(520, 536);
        canvas.moveTo(80, 500);
        canvas.lineTo(520, 500);
        canvas.moveTo(80, 464);
        canvas.lineTo(520, 464);
        canvas.moveTo(80, 428);
        canvas.lineTo(520, 428);
        canvas.stroke();
        canvas.restoreState();
        // Adding text with ColumnText.showTextAligned()
        Phrase phrase = new Phrase(foobar, times);
        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, phrase, 200, 572, 0);
        ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, phrase, 200, 536, 0);
        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, phrase, 200, 500, 0);
        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, phrase, 200, 464, 30);
        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, phrase, 200, 428, -30);
        // Chunk attributes
        c = new Chunk(foobar, times);
        c.setHorizontalScaling(0.5f);
        phrase = new Phrase(c);
        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, phrase, 400, 572, 0);
        c = new Chunk(foobar, times);
        c.setSkew(15, 15);
        phrase = new Phrase(c);
        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, phrase, 400, 536, 0);
        c = new Chunk(foobar, times);
        c.setSkew(0, 25);
        phrase = new Phrase(c);
        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, phrase, 400, 500, 0);
        c = new Chunk(foobar, times);
        phrase = new Phrase(c);
        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, phrase, 400, 464, 0);
        c = new Chunk(foobar, times);
        c.setTextRenderMode(PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE, 1, null);
        phrase = new Phrase(c);
        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, phrase, 400, 428, -0);
        // step 5
        document.close();
    }
}
