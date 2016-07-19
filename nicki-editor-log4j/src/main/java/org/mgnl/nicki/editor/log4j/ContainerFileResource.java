package org.mgnl.nicki.editor.log4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.StreamResource.StreamSource;

public class ContainerFileResource implements StreamSource {
	private static final long serialVersionUID = -2366308187343189975L;

	private static final Logger LOG = LoggerFactory.getLogger(Log4jViewer.class);

	TailViewer tailViewer;

	public ContainerFileResource(TailViewer tailViewer) {
		this.tailViewer = tailViewer;
	}

	@Override
	public InputStream getStream() {
	    try {
			PipedOutputStream pos = new PipedOutputStream();
			PipedInputStream pis = new PipedInputStream(pos);
			BeanItemContainerRenderer renderer = new BeanItemContainerRenderer(tailViewer.getContainer(), pos);
			renderer.start();
			return pis;
		} catch (IOException e) {
			LOG.error("could not read container", e);
		}
	    return null;
	}

}
