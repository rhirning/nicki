
package org.mgnl.nicki.template.loader;

/*-
 * #%L
 * nicki-template
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
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.template.engine.SimpleTemplate;
import org.mgnl.nicki.template.engine.SimpleTemplateDescriptor;

import freemarker.cache.TemplateLoader;
import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * A TemplateLoader that reads templates using classloader under a given basePath
 * as the source of templates. 
 * @author rhi
 *
 */
@Slf4j
public class ClassPathTemplateLoader implements TemplateLoader {
	
	/** The Constant APPENDIX_SEP. */
	public static final String APPENDIX_SEP = ".";
	
	/** The Constant LOCALE_SEP. */
	public static final String LOCALE_SEP = "_";
	
	/** The Constant PART_SEP. */
	public static final String PART_SEP = ".";
	
	/** The Constant PATH_SEP. */
	public static final String PATH_SEP = "/";
	
	/** The Constant DN_SEP. */
	public static final String DN_SEP = ", ";
	
	/** The Constant NAME_ATTRIBUTE. */
	public static final String NAME_ATTRIBUTE = "ou";
	
	/** The Constant PATH_ATTRIBUTE. */
	public static final String PATH_ATTRIBUTE = "ou";
	
	/** The base path. */
	private String basePath;
	
	/** The templates. */
	private Map<String, SimpleTemplate> templates = new HashMap<String, SimpleTemplate>();
	
	/**
	 * Instantiates a new class path template loader.
	 *
	 * @param basePath the base path
	 */
	public ClassPathTemplateLoader(String basePath) {
		super();
		this.basePath = basePath;
	}

	/**
	 * Close template source.
	 *
	 * @param object the object
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void closeTemplateSource(Object object) throws IOException {
		SimpleTemplateDescriptor simpleTemplateDescriptor = (SimpleTemplateDescriptor) object;
		if (this.templates.containsKey(simpleTemplateDescriptor.getName())) {
			this.templates.remove(simpleTemplateDescriptor.getName());
		}
	}

	/**
	 * path does not start with a /.
	 *
	 * @param path the path
	 * @return the simple template descriptor
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public SimpleTemplateDescriptor findTemplateSource(String path) throws IOException {
		// analyze path
		// strip appendix
		
		String handle = "/" + StringUtils.substringBeforeLast(path, APPENDIX_SEP);
		String appendix = StringUtils.substringAfterLast(path, APPENDIX_SEP);

		String directoryPath = StringUtils.substringBeforeLast(handle, PATH_SEP);
		String fullName = StringUtils.substringAfterLast(handle, PATH_SEP);
		String shortName = fullName;
		boolean hasLocale = false;
		String localeString = "";
		if (StringUtils.contains(fullName, LOCALE_SEP)) {
			hasLocale = true;
			localeString = StringUtils.substringAfter(fullName, LOCALE_SEP);
			shortName = StringUtils.substringBefore(fullName, LOCALE_SEP);
		}
		String name = shortName;
		String part = "";
		if (StringUtils.contains(shortName, PART_SEP)) {
			part = StringUtils.substringAfterLast(shortName, PART_SEP);
			name = StringUtils.substringBeforeLast(shortName, PART_SEP);
		}
		String templateName = name;
		if (hasLocale) {
			templateName += LOCALE_SEP + localeString;
		}
		
		String classPath = getClassPath(directoryPath, templateName, appendix);
//		log.debug("Search for template: " + path + " at: " + dnPath);

		return getTemplate(path, classPath, part);
	}

	/**
	 * The path defines the location of the template
	 * example: /path0/path1/name-part.ftl
	 * will be expanded to
	 *   cn=name,ou=path1,ou=path0
	 * attribute part ???
	 *
	 * @param name the name
	 * @param classPath the class path
	 * @param part the part
	 * @return the template
	 */
	public SimpleTemplateDescriptor getTemplate(String name, String classPath, String part) {
		if (this.templates.containsKey(name)) {
			return new SimpleTemplateDescriptor(templates.get(name), part);
		}
		
		SimpleTemplate template = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(SimpleTemplate.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(classPath), "iso-8859-1")) {
				template = (SimpleTemplate) jaxbUnmarshaller.unmarshal(reader);
			}
		} catch (Exception e) {
			log.info("could not read template(" + name + ", " + classPath +", " + part);
		}
		if (template != null) {
			this.templates.put(name, template);
			return new SimpleTemplateDescriptor(template, part);
		}
		return null;
	}

	/**
	 * Gets the class path.
	 *
	 * @param directoryPath the directory path
	 * @param templateName the template name
	 * @param appendix the appendix
	 * @return the class path
	 */
	private String getClassPath(String directoryPath, String templateName, String appendix) {
		return this.basePath + directoryPath + PATH_SEP + templateName + APPENDIX_SEP + appendix;
	}

	/**
	 * Gets the last modified.
	 *
	 * @param td the td
	 * @return the last modified
	 */
	public long getLastModified(Object td) {
		return new Date().getTime();
	}

	/**
	 * Gets the reader.
	 *
	 * @param templateDescriptor the template descriptor
	 * @param encoding the encoding
	 * @return the reader
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Reader getReader(Object templateDescriptor, String encoding) throws IOException {
		if (templateDescriptor instanceof SimpleTemplateDescriptor) {
			SimpleTemplateDescriptor std = (SimpleTemplateDescriptor) templateDescriptor;
			return std.getReader();
		}
		return new StringReader("");
	}

}
