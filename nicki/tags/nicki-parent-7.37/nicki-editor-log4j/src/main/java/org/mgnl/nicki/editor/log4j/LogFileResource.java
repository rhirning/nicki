package org.mgnl.nicki.editor.log4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.StreamResource.StreamSource;

public class LogFileResource implements StreamSource {
	private static final long serialVersionUID = -2366308187343189975L;

	private static final Logger LOG = LoggerFactory.getLogger(Log4jViewer.class);

	TailViewer tailViewer;

	public LogFileResource(TailViewer tailViewer) {
		this.tailViewer = tailViewer;
	}

	@Override
	public InputStream getStream() {
		File file = new File(tailViewer.getPath());
		try {
			return FileUtils.openInputStream(file);
		} catch (IOException e) {
			LOG.error("could not open file: " + tailViewer.getPath());
		}
		return null;
	}

}
