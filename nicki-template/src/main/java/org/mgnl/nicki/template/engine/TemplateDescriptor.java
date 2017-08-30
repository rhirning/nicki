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
