package org.mgnl.nicki.pdf.engine;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.mgnl.nicki.pdf.template.Pdf;
import org.mgnl.nicki.pdf.template.Pdf.Box;
import org.mgnl.nicki.pdf.template.PdfTemplate;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

public class PdfEngine {
	
    public static void main(String[] args) {
    	try {
			new PdfEngine().render("input/template.xml", "output/result.pdf");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


	public void render(String templatePath, String outputFile) throws FileNotFoundException, DocumentException {

		PdfTemplate template = new PdfTemplate(templatePath);
		Pdf pdf = template.getPdf();
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFile));
        document.open();
        
        document.setPageSize(PageSize.A4);
        PdfContentByte cb = writer.getDirectContent();
        if (pdf.getBox()!= null) {
        	for (Box box : pdf.getBox()) {
				render(cb, box);
			}
        }

        document.close();
	}

	private void render(PdfContentByte cb, Box box) {

        Font font = new Font(Font.HELVETICA);
        BaseFont bf = font.getCalculatedBaseFont(false);
        cb.setFontAndSize(bf, 12);
		cb.showTextAligned(Element.ALIGN_LEFT, box.getText(), box.getX(), box.getY(), 0);
	}

}
