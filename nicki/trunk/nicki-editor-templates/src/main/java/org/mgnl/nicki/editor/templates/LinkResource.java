package org.mgnl.nicki.editor.templates;

import com.vaadin.Application;
import com.vaadin.terminal.StreamResource;

public class LinkResource extends StreamResource {
	private String mimeType = "";
	public LinkResource(StreamSource streamSource, String filename,
			Application application, String mimeType) {
		super(streamSource, filename, application);
		this.mimeType = mimeType;
		setCacheTime(-1);
	}

	private static final long serialVersionUID = -426896041747116523L;

	@Override
	public String getMIMEType() {
		return mimeType;
	}

}
