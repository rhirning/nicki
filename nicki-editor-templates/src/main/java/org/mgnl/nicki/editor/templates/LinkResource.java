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

import com.vaadin.Application;
import com.vaadin.terminal.StreamResource;

public class LinkResource extends StreamResource {
	private String mimeType = "";
	public LinkResource(StreamSource streamSource, String filename,
			Application application, String mimeType) {
		super(streamSource, filename, application);
		this.mimeType = mimeType;
		setCacheTime(1);
	}

	private static final long serialVersionUID = -426896041747116523L;

	@Override
	public String getMIMEType() {
		return mimeType;
	}

}
