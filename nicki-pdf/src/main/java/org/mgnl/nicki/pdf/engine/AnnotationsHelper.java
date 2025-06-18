
package org.mgnl.nicki.pdf.engine;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfWriter;


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



/**
 * The Class AnnotationsHelper.
 */
public class AnnotationsHelper {

    /**
     * Adds the annotation.
     *
     * @param writer the writer
     * @param position the position
     * @param title the title
     * @param text the text
     */
    public static void addAnnotation(PdfWriter writer, Rectangle position, String title, String text) {
    	Rectangle rect = new Rectangle(
    			position.getRight() + 10, position.getBottom(),
    			position.getRight() + 30, position.getTop());
        PdfAnnotation annotation = PdfAnnotation.createText(writer, rect, title, text, false, "Comment");
        writer.addAnnotation(annotation);
    }

}
