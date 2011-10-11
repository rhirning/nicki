package org.mgnl.nicki.editor.templates;

import java.io.InputStream;

import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.template.engine.BasicTemplateStreamSource;

import com.vaadin.terminal.StreamResource.StreamSource;

public class CsvStreamSource extends BasicTemplateStreamSource implements StreamSource {
	private static final long serialVersionUID = 4222973194514516918L;

	
	public CsvStreamSource(Template template, NickiContext context, Object params) {
		super(template, context, params);
	}

	@Override
	public InputStream getStream() {
		return getCsVStream();
	}
	

}
