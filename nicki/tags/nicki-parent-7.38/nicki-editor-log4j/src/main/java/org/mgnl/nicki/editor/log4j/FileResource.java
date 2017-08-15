package org.mgnl.nicki.editor.log4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.StreamResource.StreamSource;

public class FileResource implements StreamSource {
	private static final long serialVersionUID = -2366308187343189975L;

	private static final Logger LOG = LoggerFactory.getLogger(Log4jViewer.class);

	private File file;
	
	public FileResource(File file) {
		this.file = file;
	}

	@Override
	public InputStream getStream() {
		try {
			return FileUtils.openInputStream(file);
		} catch (IOException e) {
			LOG.error("could not open file: " + file);
		}
		return null;
	}

}
