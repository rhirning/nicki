/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
