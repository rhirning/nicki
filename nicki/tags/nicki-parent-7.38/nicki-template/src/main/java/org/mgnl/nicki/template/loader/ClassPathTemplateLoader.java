/**
 * Copyright (c) 2003-2015 Dr. Ralf Hirning
 * All rights reserved.
 *  
 * This program is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 * 
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU Public License v3.0
 * which is available at
 * http://www.gnu.org/licenses/gpl.html
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 * 
 * Any modifications to this file must keep this entire header
 * intact.
*/
package org.mgnl.nicki.template.loader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.template.engine.SimpleTemplate;
import org.mgnl.nicki.template.engine.SimpleTemplateDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.TemplateLoader;

/**
 * A TemplateLoader that reads templates using classloader under a given basePath
 * as the source of templates. 
 * @author rhi
 *
 */
public class ClassPathTemplateLoader implements TemplateLoader {
	private static final Logger LOG = LoggerFactory.getLogger(ClassPathTemplateLoader.class);
	
	public static final String APPENDIX_SEP = ".";
	public static final String LOCALE_SEP = "_";
	public static final String PART_SEP = ".";
	public static final String PATH_SEP = "/";
	public static final String DN_SEP = ", ";
	public static final String NAME_ATTRIBUTE = "ou";
	public static final String PATH_ATTRIBUTE = "ou";
	
	private String basePath;
	private Map<String, SimpleTemplate> templates = new HashMap<String, SimpleTemplate>();
	
	public ClassPathTemplateLoader(String basePath) {
		super();
		this.basePath = basePath;
	}

	public void closeTemplateSource(Object object) throws IOException {
		SimpleTemplateDescriptor simpleTemplateDescriptor = (SimpleTemplateDescriptor) object;
		if (this.templates.containsKey(simpleTemplateDescriptor.getName())) {
			this.templates.remove(simpleTemplateDescriptor.getName());
		}
	}

	/**
	 * path does not start with a /
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
//		LOG.debug("Search for template: " + path + " at: " + dnPath);

		return getTemplate(path, classPath, part);
	}

	/**
	 * 
	 * The path defines the location of the template
	 * example: /path0/path1/name-part.ftl
	 * will be expanded to
	 *   cn=name,ou=path1,ou=path0
	 * attribute part ???
	 * @throws IOException 
	 * @throws NamingException 
	 * 
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
			LOG.info("could not read template(" + name + ", " + classPath +", " + part);
		}
		if (template != null) {
			this.templates.put(name, template);
			return new SimpleTemplateDescriptor(template, part);
		}
		return null;
	}

	private String getClassPath(String directoryPath, String templateName, String appendix) {
		return this.basePath + directoryPath + PATH_SEP + templateName + APPENDIX_SEP + appendix;
	}

	public long getLastModified(Object td) {
		return new Date().getTime();
	}

	public Reader getReader(Object templateDescriptor, String encoding) throws IOException {
		if (templateDescriptor instanceof SimpleTemplateDescriptor) {
			SimpleTemplateDescriptor std = (SimpleTemplateDescriptor) templateDescriptor;
			return std.getReader();
		}
		return new StringReader("");
	}

}
