
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

import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.pdf.engine.Point;
import org.mgnl.nicki.pdf.model.config.Configuration;

// TODO: Auto-generated Javadoc
/**
 * The Class PdfConfiguration.
 */
@Slf4j
public class PdfConfiguration {

	/** The Constant CM_PER_INCH. */
	public static final float CM_PER_INCH = 2.54f;
	
	/** The Constant MM_PER_USER_UNIT. */
	public static final float MM_PER_USER_UNIT = CM_PER_INCH * 10.0f / 72.0f;
	
	/** The config. */
	private Configuration config;
	
	/** The default font. */
	//private Map<String, BaseFont> fonts = new HashMap<String, BaseFont>();
	private Font defaultFont;
	
	/** The context base path. */
	private String contextBasePath;

	/**
	 * Instantiates a new pdf configuration.
	 *
	 * @param configuration the configuration
	 * @param contextBasePath the context base path
	 * @throws JAXBException the JAXB exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public PdfConfiguration(InputStream configuration, String contextBasePath) throws JAXBException, IOException {
		config = unmarshal(Configuration.class, configuration);
		if(!StringUtils.endsWith(contextBasePath, "/")) {
			contextBasePath = contextBasePath + "/";
		}
		
		this.contextBasePath = contextBasePath;
	}
	
	/**
	 * Inits the.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void init() throws IOException {
		
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

	/**
	 * Unmarshal.
	 *
	 * @param <T> the generic type
	 * @param docClass the doc class
	 * @param inputStream the input stream
	 * @return the t
	 * @throws JAXBException the JAXB exception
	 */
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
	
	/**
	 * Gets the configuration.
	 *
	 * @return the configuration
	 */
	public Configuration getConfiguration() {
		return config;
	}
	
	/**
	 * Sets the configuration.
	 *
	 * @param config the new configuration
	 */
	public void setConfiguration(Configuration config) {
		this.config = config;
	}
	
	/**
	 * Gets the font.
	 *
	 * @param font the font
	 * @param size the size
	 * @param style the style
	 * @return the font
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Font getFont(String font, Integer size, FontStyle style) throws IOException {
		return getFont(defaultFont, font, size, style);
	}
	
	/**
	 * Gets the font.
	 *
	 * @param base the base
	 * @param font the font
	 * @param size the size
	 * @param style the style
	 * @return the font
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Font getFont(Font base, String font, Integer size, FontStyle style) throws IOException {
		
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
	
	/**
	 * Gets the font.
	 *
	 * @param font the font
	 * @param size the size
	 * @return the font
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Font getFont(String font, int size) throws IOException {
		return getFont(font, size, FontStyle.NORMAL);
	}
	
	
	/**
	 * Transform point.
	 *
	 * @param point the point
	 * @return the point
	 */
	public Point transformPoint(Point point) {
		return new Point(transform(point.getX()), (int) transform(point.getY()));
	}

	/**
	 * Transform.
	 *
	 * @param value the value
	 * @return the float
	 */
	public float transform(float value) {
		return value / PdfConfiguration.MM_PER_USER_UNIT;
	}

	/**
	 * Un transform.
	 *
	 * @param value the value
	 * @return the float
	 */
	public float unTransform(float value) {
		return value * PdfConfiguration.MM_PER_USER_UNIT;
	}
	
	/**
	 * Gets the image.
	 *
	 * @param relativePath the relative path
	 * @return the image
	 */
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
