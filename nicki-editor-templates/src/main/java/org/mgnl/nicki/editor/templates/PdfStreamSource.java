/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
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
 */
package org.mgnl.nicki.editor.templates;

import java.io.InputStream;
import java.util.Map;

import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.template.engine.BasicTemplateStreamSource;

import com.vaadin.terminal.StreamResource.StreamSource;

public class PdfStreamSource extends BasicTemplateStreamSource implements StreamSource {
	private static final long serialVersionUID = 4222973194514516918L;
	public PdfStreamSource(Template template, NickiContext context, Map<String, Object> params) {
		super(template, context, params);
	}

	public InputStream getStream() {
		return getPdfStream();
	}

}
