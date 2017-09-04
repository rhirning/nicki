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
