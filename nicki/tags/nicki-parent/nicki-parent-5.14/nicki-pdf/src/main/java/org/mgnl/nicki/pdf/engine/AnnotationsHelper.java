package org.mgnl.nicki.pdf.engine;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfWriter;

public class AnnotationsHelper {

    public static void addAnnotation(PdfWriter writer, Rectangle position, String title, String text) {
    	Rectangle rect = new Rectangle(
    			position.getRight() + 10, position.getBottom(),
    			position.getRight() + 30, position.getTop());
        PdfAnnotation annotation = PdfAnnotation.createText(writer, rect, title, text, false, "Comment");
        writer.addAnnotation(annotation);
    }

}
