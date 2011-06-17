package org.mgnl.nicki.template.engine;

import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.lang.StringUtils;

public class TemplateDescriptor {
	private Template template;
	private String part;
	public TemplateDescriptor(Template template, String part) {
		super();
		this.template = template;
		this.part = part;
	}
	public Reader getReader() {
		if (StringUtils.isNotEmpty(part)) {
			if (template.containsKey(part)) {
				return new StringReader(template.getPart(part));
			}
			return new StringReader("");
		} else {
			return new StringReader(template.getData());
		}
	}
	public String getName() {
		return this.template.getName();
	}
}
