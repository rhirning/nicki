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
import java.io.Reader;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.template.engine.Template;
import org.mgnl.nicki.template.engine.TemplateDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.TemplateLoader;

/**
 * A TemplateLoader that uses JNDI objects in a directory located in a given baseDN
 * as the source of templates. 
 * @author rhi
 *
 */
public class JndiTemplateLoader implements TemplateLoader {
	private static final Logger LOG = LoggerFactory.getLogger(JndiTemplateLoader.class);
	
	public static final String APPENDIX_SEP = ".";
	public static final String LOCALE_SEP = "_";
	public static final String PART_SEP = ".";
	public static final String PATH_SEP = "/";
	public static final String DN_SEP = ", ";
	public static final String NAME_ATTRIBUTE = "ou";
	public static final String PATH_ATTRIBUTE = "ou";
	
	private String baseDN;
	private Map<String, Template> templates = new HashMap<String, Template>();
	private NickiContext context;
	
	public JndiTemplateLoader(NickiContext context, String baseDN) {
		super();
		this.context = context;
		this.baseDN = baseDN;
	}

	public void closeTemplateSource(Object object) throws IOException {
		TemplateDescriptor td = (TemplateDescriptor) object;
		if (this.templates.containsKey(td.getName())) {
			this.templates.remove(td.getName());
		}
	}

	/**
	 * path does not start with a /
	 */
	public TemplateDescriptor findTemplateSource(String path) throws IOException {
		// analyze path
		// strip appendix
		String handle = "/" + StringUtils.substringBeforeLast(path, APPENDIX_SEP);

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
		
		String dnPath = getDnPath(directoryPath, templateName);
//		LOG.debug("Search for template: " + path + " at: " + dnPath);

		return getTemplate(context, path, dnPath, part);
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
	public TemplateDescriptor getTemplate(NickiContext context, String name, String dnPath, String part) {
		if (this.templates.containsKey(name)) {
			return new TemplateDescriptor(templates.get(name), part);
		}

		// now get the template from JNDI Source
		Template template = null;
		try {
			TemplateLoaderLdapQueryHandler handler = new TemplateLoaderLdapQueryHandler(context, name, dnPath);
			context.search(handler);
			template = handler.getTemplate();
		} catch (DynamicObjectException e) {
			LOG.debug("Error", e);
		}
		if (template != null) {
			this.templates.put(name, template);
			return new TemplateDescriptor(template, part);
		}
		return null;
	}

	private String getDnPath(String directoryPath, String templateName) {
		String dirParts[] = StringUtils.split(directoryPath, PATH_SEP);
		StringBuilder sb = new StringBuilder();
		for (int i = dirParts.length -1; i >= 0; i--) {
			if (StringUtils.isNotEmpty(dirParts[i])) {
					sb.append(DN_SEP);
				sb.append(PATH_ATTRIBUTE).append("=").append(dirParts[i]);
			}
		}
		return NAME_ATTRIBUTE + "=" + templateName + sb.toString() + "," + baseDN;
	}

	public long getLastModified(Object td) {
		return new Date().getTime();
	}

	public Reader getReader(Object templateDescriptor, String encoding) throws IOException {
		if (templateDescriptor instanceof TemplateDescriptor) {
			TemplateDescriptor td = (TemplateDescriptor) templateDescriptor;
			return td.getReader();
		}
		return new StringReader("");
	}

}
