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

	@Override
	public InputStream getStream() {
		return getPdfStream();
	}

}