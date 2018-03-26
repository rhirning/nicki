
package org.mgnl.nicki.pdf.configuration;

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

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.pdf.engine.Point;
import org.mgnl.nicki.pdf.model.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PdfConfiguration {

	private static final Logger log = LoggerFactory.getLogger(PdfConfiguration.class);
	public static final float CM_PER_INCH = 2.54f;
	public static final float MM_PER_USER_UNIT = CM_PER_INCH * 10.0f / 72.0f;
	
	private Configuration config;
	//private Map<String, BaseFont> fonts = new HashMap<String, BaseFont>();
	private Font defaultFont;
	private String contextBasePath;

	public PdfConfiguration(InputStream configuration, String contextBasePath) throws JAXBException, DocumentException, IOException {
		config = unmarshal(Configuration.class, configuration);
		if(!StringUtils.endsWith(contextBasePath, "/")) {
			contextBasePath = contextBasePath + "/";
		}
		
		this.contextBasePath = contextBasePath;
	}
	
	public void init() throws DocumentException, IOException {
		
		for (org.mgnl.nicki.pdf.model.config.Font font : config.getFonts().getFont()) {
			log.debug("registering font {}", contextBasePath + font.getValue());
			FontFactory.register(contextBasePath + font.getValue(), font.getName());
		}
		log.debug("setting default font: {}, {}, {}", new Object[] {
				config.getFonts().getDefault().getFont(), 
				config.getFonts().getDefault().getSize(), 
				FontStyle.byName(config.getFonts().getDefault().getStyle())});
		defaultFont = getFont(
				FontFactory.getFont("Times", 10f, Font.NORMAL),
				config.getFonts().getDefault().getFont(), 
				config.getFonts().getDefault().getSize(), 
				FontStyle.byName(config.getFonts().getDefault().getStyle()));
		
	}

	private <T> T unmarshal(Class<T> docClass, InputStream inputStream)
			throws JAXBException {
		String packageName = docClass.getPackage().getName();
		JAXBContext jc = JAXBContext.newInstance(packageName);
		Unmarshaller u = jc.createUnmarshaller();
		Object o = u.unmarshal(inputStream);
		log.debug("class: " + o.getClass());
		@SuppressWarnings("unchecked")
		T doc = (T) o;
		return doc;
	}
	
	public Configuration getConfiguration() {
		return config;
	}
	
	public void setConfiguration(Configuration config) {
		this.config = config;
	}
	
	public Font getFont(String font, Integer size, FontStyle style) throws DocumentException, IOException {
		return getFont(defaultFont, font, size, style);
	}
	
	public Font getFont(Font base, String font, Integer size, FontStyle style) throws DocumentException, IOException {
		
		if(base == null) {
			base = new Font(defaultFont);
		}
		
		Font f = new Font(base);
		
		if(font != null) {
			f = FontFactory.getFont(font);
			f.setSize(base.getSize());
			f.setStyle(base.getStyle());
		}
		
		if(size != null) {
			f.setSize((float) size);
		}
		
		if(style != null) {
			f.setStyle(style.getFontStyle());
		}
		log.debug("return font: " + f.getFamilyname() + ", size: " + f.getSize() + ", style: " + f.getStyle());
		return f;
	}
	
	public Font getFont(String font, int size) throws DocumentException, IOException {
		return getFont(font, size, FontStyle.NORMAL);
	}
	
	
	public Point transformPoint(Point point) {
		return new Point(transform(point.getX()), (int) transform(point.getY()));
	}

	public float transform(float value) {
		return value / PdfConfiguration.MM_PER_USER_UNIT;
	}

	public float unTransform(float value) {
		return value * PdfConfiguration.MM_PER_USER_UNIT;
	}
	
	public Image getImage(String relativePath) {
		Image image = null;
		
		try {
			InputStream in = getClass().getClassLoader().getResourceAsStream(relativePath);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			IOUtils.copy(in, out);
			image = Image.getInstance(out.toByteArray());
		} catch (Exception ex) {
			log.info("could not find image {}", relativePath);
			// do nothing, return null
		}
		return image;
	}
}
