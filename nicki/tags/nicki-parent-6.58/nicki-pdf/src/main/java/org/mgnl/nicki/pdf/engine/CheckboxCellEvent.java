package org.mgnl.nicki.pdf.engine;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.pdf.model.template.Checkbox;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RadioCheckField;


class CheckboxCellEvent implements PdfPCellEvent {
    // The name of the check box field
    protected Checkbox checkbox;
    // We create a cell event
    public CheckboxCellEvent(Checkbox checkbox) {
        this.checkbox = checkbox;
    }
    // We create and add the check box field
    @Override
    public void cellLayout(PdfPCell cell, Rectangle position,
        PdfContentByte[] canvases) {
        PdfWriter writer = canvases[0].getPdfWriter(); 
        // define the coordinates of the middle
        float x = (position.getLeft() + position.getRight()) / 2;
        float y = (position.getTop() + position.getBottom()) / 2;
        // define the position of a check box that measures 20 by 20
        Rectangle rect = new Rectangle(x - 4, y - 4, x + 4, y + 4);
        // define the check box
        RadioCheckField cb = new RadioCheckField(
                writer, rect, checkbox.getName(), "Yes");
        
        if (StringUtils.isNotBlank(checkbox.getAnnotation())) {
        	AnnotationsHelper.addAnnotation(writer, position, "title", checkbox.getAnnotation());
        }
        // add the check box as a field
        try {
            writer.addAnnotation(cb.getCheckField());
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

}
